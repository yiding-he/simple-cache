package com.hyd.simplecache;

/**
 * 带权重的服务器地址
 *
 * @author 贺一丁
 */
public class WeightedAddress {

    private String host;

    private int port;

    private int weight;

    public WeightedAddress() {
    }

    public WeightedAddress(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "WeightedAddress{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                '}';
    }
}
