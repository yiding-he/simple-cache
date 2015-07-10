package com.hyd.simplecache;

import org.junit.Test;

import java.util.List;

/**
 * (description)
 * created at 2015/6/12
 *
 * @author Yiding
 */
public class MemcachedConfigurationTest {

    @Test
    public void testPsrseAddressList() throws Exception {
        String addresses = "127.0.0.1:2345,127.0.0.1:6789;192.168.22.33:1234;;,,";
        List<WeightedAddress> list = MemcachedConfiguration.psrseAddressList(addresses);
        System.out.println("size: " + list.size());
        for (WeightedAddress address : list) {
            System.out.println(address);
        }
    }
}