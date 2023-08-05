package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 添加用户
     * @param employee
     */
    void addEmp(Employee employee);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pagQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工账户信息
     * @return
     */
    void statusAndStop(Employee employee);

    @Select("select * from employee where id = #{id};")
    Employee getByIdEmp(Long id);
}
