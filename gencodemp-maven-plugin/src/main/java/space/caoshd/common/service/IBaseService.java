package space.caoshd.common.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.io.Serializable;
import java.util.List;

public interface IBaseService<PO, BO> {
    void insert(BO bo);

    void delete(BO bo);

    void deleteById(Serializable id);

    int update(BO bo, PO query);

    BO findById(Serializable id);

    BO findOne(PO query);

    List<BO> list();

    List<BO> list(PO query);

    List<BO> list(QueryWrapper<PO> query);

    Long count(QueryWrapper<PO> query);

    Long count(PO query);

    BO convertToBO(PO po);

    PO convertToPO(BO bo);
}
