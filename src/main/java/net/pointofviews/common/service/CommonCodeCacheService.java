package net.pointofviews.common.service;

import net.pointofviews.common.domain.CommonCode;

import java.util.List;

public interface CommonCodeCacheService {
    List<CommonCode> findAll();
}
