package com.xinkao.erp.summary.service.Impl;

import com.xinkao.erp.common.service.impl.BaseServiceImpl;
import com.xinkao.erp.summary.entity.Shape;
import com.xinkao.erp.summary.mapper.ShapeMapper;
import com.xinkao.erp.summary.service.ShapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShapeServiceImpl extends BaseServiceImpl<ShapeMapper, Shape> implements ShapeService {
    @Autowired
    private ShapeMapper shapeMapper;
}
