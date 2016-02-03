package net.kalish.hologram.service;

/**
 * todo: replace with a real config mechanism, currently thinking typesafe
 * todo: due to its compatibility with properties files and nice parsing
 * todo: of values like 512k
 *
 * https://github.com/typesafehub/config
 */
public class LazyConfig {
    public static int DEFAULT_PORT = 8989;
    public static int MASTER_TO_SLAVE_PORT = 8990;
    public static int DISRUPTOR_SIZE = 4096;
}
