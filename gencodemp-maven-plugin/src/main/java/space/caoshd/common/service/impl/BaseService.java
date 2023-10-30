package space.caoshd.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;
import space.caoshd.common.service.IBaseService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseService<PO, BO> implements IBaseService<PO, BO> {

    private BaseMapper<PO> baseMapper;
    private Class<BO> boClass;

    private Class<PO> poClass;

    public void setBaseMapper(BaseMapper<PO> baseMapper) {
        this.baseMapper = baseMapper;
    }

    public BaseMapper<PO> getBaseMapper() {
        return baseMapper;
    }

    public void setBoClass(Class<BO> boClass) {
        this.boClass = boClass;
    }

    public void setPoClass(Class<PO> poClass) {
        this.poClass = poClass;
    }

    @Override
    public void insert(BO bo) {
        PO po = convertToPO(bo);
        baseMapper.insert(po);
        BeanUtils.copyProperties(po, bo);
    }

    @Override
    public void delete(BO bo) {
        baseMapper.delete(new QueryWrapper<>(convertToPO(bo)));
    }

    @Override
    public void deleteById(Serializable id) {
        baseMapper.deleteById(id);
    }

    @Override
    public int update(BO bo, PO query) {
        return baseMapper.update(convertToPO(bo), new QueryWrapper<>(query));
    }

    @Override
    public BO findById(Serializable id) {
        return convertToBO(baseMapper.selectById(id));
    }

    @Override
    public BO findOne(PO query) {
        return convertToBO(baseMapper.selectOne(new QueryWrapper<>(query)));
    }

    @Override
    public List<BO> list() {
        return convertList(baseMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public List<BO> list(QueryWrapper<PO> query) {
        return convertList(baseMapper.selectList(query));
    }

    @Override
    public List<BO> list(PO query) {
        List<PO> pos = baseMapper.selectList(new QueryWrapper<>(query));
        return convertList(pos);
    }

    private List<BO> convertList(List<PO> pos) {
        List<BO> result = new ArrayList<>();
        for (PO po : pos) {
            result.add(convertToBO(po));
        }
        return result;
    }

    @Override
    public Long count(QueryWrapper<PO> query) {
        return baseMapper.selectCount(query);
    }

    @Override
    public Long count(PO query) {
        return baseMapper.selectCount(new QueryWrapper<>(query));
    }

    @Override
    public BO convertToBO(PO po) {
        try {
            if (po == null) {
                return null;
            }
            BO bo = boClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(po, bo);
            return bo;
        } catch (Exception e) {
            throw new RuntimeException("数据传输对象转换异常", e);
        }
    }

    @Override
    public PO convertToPO(BO bo) {
        if (bo == null) {
            return null;
        }
        try {
            PO po = poClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(bo, po);
            return po;
        } catch (Exception e) {
            throw new RuntimeException("数据传输对象转换异常", e);
        }
    }
}
