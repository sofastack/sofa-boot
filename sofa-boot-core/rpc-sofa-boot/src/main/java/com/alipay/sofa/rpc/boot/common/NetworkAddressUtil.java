/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.sofa.rpc.boot.common;

import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 地址处理类
 *
 * @author luoguimu123
 * @version $Id: NetworkAddressUtil.java, v 0.1 2017-08-01 8:30 luoguimu123 Exp $
 */
public class NetworkAddressUtil {

    private static final char      COLON                = ':';

    private static final Logger    logger               = LoggerFactory
                                                            .getLogger(NetworkAddressUtil.class);

    protected static List<IpRange> IP_RANGES            = null;
    private static String          NETWORK_ADDRESS;
    private static String          BIND_NETWORK_ADDRESS = null;
    private static String          HOST_NAME;

    private static String          DEFAULT_HOST_NAME    = "app";

    /**
     * this method should be invoked fisrt
     *
     * @param enabledIpRange
     * @param bindNetworkInterface
     */
    public static void caculate(String enabledIpRange, String bindNetworkInterface) {
        IP_RANGES = new CopyOnWriteArrayList<IpRange>();
        if (StringUtils.isEmpty(enabledIpRange)) { // 没有设置bind_network_interface
            // 默认支持所有的ip端口
            IP_RANGES.add(new IpRange("0", "255"));
            // ip_range 为空的时候，bind 的地址为 0.0.0.0
            BIND_NETWORK_ADDRESS = "0.0.0.0";
        } else {
            String[] ipRanges = enabledIpRange.split(",");
            for (String ipRange : ipRanges) {
                if (StringUtils.isEmpty(ipRange)) {
                    continue;
                }

                if (ipRange.indexOf(COLON) > -1) {
                    String[] ranges = ipRange.split(":");
                    IP_RANGES.add(new IpRange(ranges[0], ranges[1]));
                } else {
                    IP_RANGES.add(new IpRange(ipRange));
                }
            }
        }

        NETWORK_ADDRESS = getNetworkAddress(bindNetworkInterface);
        HOST_NAME = getHostName();
    }

    /**
     * 获得本地的网络地址
     * <p>
     * 在有超过一块网卡时有问题，这里每次只取了第一块网卡绑定的IP地址
     * 当存在这种情况的时候，就需要配置 rpc_enabled_ip_range 参数，用以限制IP范围
     *
     * @return 本地的 IP 地址
     */
    public static String getNetworkAddress(String bindNetworkInterface) {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                boolean useNi = false;
                NetworkInterface ni = netInterfaces.nextElement();
                if (!StringUtils.isBlank(bindNetworkInterface)) {
                    if (bindNetworkInterface.equals(ni.getDisplayName()) || bindNetworkInterface.equals(ni.getName())) {
                        useNi = true;
                    } else {
                        continue;
                    }
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(COLON) == -1
                        && (useNi || ipEnabled(ip.getHostAddress()))) {
                        return ip.getHostAddress();
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return 获得本机唯一 IP 地址
     */
    public static String getLocalIP() {
        return NETWORK_ADDRESS;
    }

    /**
     * ip_range 为空的时候且没有设置 bind_network_interface 时，bind 的地址为 0.0.0.0
     *
     * @return 获得本机监听 IP 地址
     */
    public static String getLocalBindIP() {
        if (BIND_NETWORK_ADDRESS != null) {
            return BIND_NETWORK_ADDRESS;
        }

        return NETWORK_ADDRESS;
    }

    /**
     * @return 获得本机唯一 hostname
     */
    public static String getLocalHostName() {
        return HOST_NAME;
    }

    /**
     * @return 获得主机名称
     */
    private static String getHostName() {
        String hostName = DEFAULT_HOST_NAME;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            //ignore
            hostName = DEFAULT_HOST_NAME;
        }
        return hostName;
    }

    /**
     * 判断 IP 是否符合要求
     *
     * @param ip ip地址，XXX.XXX.XXX.XXX
     * @return 是否符合要求
     */
    public static boolean ipEnabled(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }

        // 如果IP_RANGES为空，返回true
        if (IP_RANGES.isEmpty()) {
            return true;
        }

        // 遍历IP_RANGES
        for (IpRange ipRange : IP_RANGES) {
            if (ipRange.isEnabled(ip)) {
                return true;
            }
        }

        return false;
    }

    /**
     * IP 范围
     */
    protected static class IpRange {
        private long start;
        private long end;

        public IpRange(String ip) {
            start = parseStart(ip);
            end = parseEnd(ip);
        }

        public IpRange(String startIp, String endIp) {
            start = parseStart(startIp);
            end = parseEnd(endIp);
        }

        private long parseStart(String ip) {
            int[] starts = { 0, 0, 0, 0 };
            return parse(starts, ip);
        }

        private long parseEnd(String ip) {
            int[] ends = { 255, 255, 255, 255 };
            return parse(ends, ip);
        }

        private long parse(int[] segments, String ip) {
            String[] ipSegments = ip.split("\\.");
            for (int i = 0; i < ipSegments.length; i++) {
                segments[i] = Integer.parseInt(ipSegments[i]);
            }
            long ret = 0;
            for (int i : segments) {
                ret += ret * 255L + i;
            }
            return ret;
        }

        public boolean isEnabled(String ip) {
            String[] ipSegments = ip.split("\\.");
            long ipInt = 0;
            for (String ipSegment : ipSegments) {
                ipInt += ipInt * 255L + Integer.parseInt(ipSegment);
            }
            return ipInt >= start && ipInt <= end;
        }
    }
}
