package com.example.demo.dao.impl;

import com.example.demo.dao.BaseRepository;
import com.example.demo.entity.Employee;
import com.example.demo.query.EmpQuery;
import com.example.demo.query.ResultData;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

public class EmployeeRepositoryImpl implements BaseRepository<Employee, EmpQuery> {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 处理分页和过滤查询的自定方法
     * @param query
     * @return
     */
    @Override
    public ResultData<Employee> searchByQuery(EmpQuery query) {

        String s = splitSql(query);
        int page = query.getPage();
        int pageSize = query.getPageSize();
        String limitSql = " limit " + (page - 1) + "," + pageSize;

        StringBuilder sb = new StringBuilder("select * from (select A.e_id id,A.e_portrait portrait,A.e_pwd pwd,A.e_name name,A.e_uname uname,A.e_birthday birthday,A.e_level level,A.e_six six,A.e_wages wages,A.e_hobby hobby,A.e_mibiao mibiao,A.e_midaan midaan,A.e_add eAdd,A.e_update eUpdate,A.e_login login,A.e_xzlogin xzlogin,A.e_remark remark,B.d_id deptid,B.d_name dname,B.d_location location,B.d_wei wei from emp A left join dept B on A.e_deptId=B.d_id) T ");
        sb.append(s);
        sb.append(limitSql);
        Query nativeQuery = entityManager.createNativeQuery(sb.toString());

        nativeQuery.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List resultList = nativeQuery.getResultList();

        Query countQuery = entityManager.createNativeQuery("select count(id) from (select A.e_id id,A.e_portrait portrait,A.e_pwd pwd,A.e_name name,A.e_uname uname,A.e_birthday birthday,A.e_level level,A.e_six six,A.e_wages wages,A.e_hobby hobby,A.e_mibiao mibiao,A.e_midaan midaan,A.e_add eAdd,A.e_update eUpdate,A.e_login login,A.e_xzlogin xzlogin,A.e_remark remark,B.d_id deptid,B.d_name dname,B.d_location location,B.d_wei wei from emp A left join dept B on A.e_deptId=B.d_id) T " + s);
        List<BigInteger> countList = countQuery.getResultList();
        BigInteger totalCount = null;
        if (countList != null) {
            totalCount = countList.get(0);
        }

        ResultData<Employee> resultData = new ResultData();
        resultData.setList(resultList);
        resultData.setPage(page);
        resultData.setPageSize(pageSize);
        resultData.setTotalCount(totalCount);

        return resultData;
    }

    /**
     * 将前台传来的参数进行sql拼接
     * @param query
     * @return
     */
    public String splitSql(EmpQuery query) {
        String dname = query.getDname();
        String wei = query.getWei();
        String location = query.getLocation();
        String hobby = query.getHobby();
        String level = query.getLevel();
        String six = query.getSix();
        String uname = query.getUname();
        StringBuilder sb = new StringBuilder();
        if (!(dname == null || dname.equals("-1"))) {
            sb.append(" and dname='" + dname + "'");
        }
        if (!(wei == null || wei.equals("-1"))) {
            sb.append(" and wei='" + wei + "'");
        }
        if (!(location == null || location.equals("-1"))) {
            sb.append(" and location='" + location + "'");
        }
        if (!(hobby == null || hobby.equals("-1"))) {
            sb.append(" and hobby='" + hobby + "'");
        }
        if (!(level == null || level.equals("-1"))) {
            sb.append(" and level=" + level);
        }
        if (!(six == null || six.equals("-1"))) {
            sb.append(" and six=" + six);
        }
        if (!(uname == null || uname.equals(""))) {
            sb.append(" and uname='" + uname + "'");
        }
        String s1 = sb.toString().replaceFirst("and", "where");
        String age = query.getAge();
        String eUpate = query.getuDateSort();
        StringBuilder sb2 = new StringBuilder();
        if (!(eUpate == null || eUpate.equals("-1"))) {
            if (eUpate.equals("1")) {
                sb2.append(",eUpdate desc");
            } else {
                sb2.append(",eUpdate asc");
            }
        }
        if (!(age == null || age.equals("-1"))) {
            if (age.equals("1")) {
                sb2.append(",birthday desc");
            } else {
                sb2.append(",birthday asc");
            }
        }
        String s2 = sb2.toString().replaceFirst(",", "");
        if (!s2.equals("")) {
            s2 = " order by " + s2;
        }
        return s1 + s2;
    }

}
