package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     */
    void addEmp(EmployeeDTO employeeDTO);

    /**
     * 员工的分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pagingSelect(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 账户的启动和禁用
     * @param status
     * @param id
     * @return
     */
    void statusAndStop(Integer status, Long id);

    /**
     * 修改员工
     * @param employeeDTO
     */
    void updateEmp(EmployeeDTO employeeDTO);

    /**
     * 查询员工根据id
     * @param id
     * @return
     */
    Employee getByIdEmp(Long id);
}
