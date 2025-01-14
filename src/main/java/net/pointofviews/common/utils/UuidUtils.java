package net.pointofviews.common.utils;

import net.pointofviews.common.exception.UuidException;

import java.util.UUID;

public class UuidUtils {
    private UuidUtils() {}

    public static UUID fromString(String uuid) {
        try{
            return UUID.fromString(uuid);
        }catch (Exception e) {
            throw UuidException.invalidUuid(uuid);
        }
    }
}
