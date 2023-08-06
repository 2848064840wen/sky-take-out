package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对密码进行MD5处理
        // 密码:123456 加密后:e10adc3949ba59abbe56e057f20f883e
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        log.info("前端用户加密后的密码:------>   " + password);

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     */
    @Override
    public void addEmp(EmployeeDTO employeeDTO) {
        // 把employeeLoginDTO 转为实体类
        Employee employee = new Employee();

        // 使用工具类进行属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置额外的属性

        // 设置账号的状态
        employee.setStatus(StatusConstant.ENABLE);

        // 设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        log.info("封装后的emp：   " + employee);

        // 添加到数据库
        employeeMapper.addEmp(employee);
    }


    /**
     * 员工的分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pagingSelect(EmployeePageQueryDTO employeePageQueryDTO) {

        // 开始分页
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        Page<Employee> page = employeeMapper.pagQuery(employeePageQueryDTO);

        // 获取总记录数
        long total = page.getTotal();
        // 获取分页后的结果
        List<Employee> result = page.getResult();

        return new PageResult(total, result);
    }

    /**
     * 账户的启动和禁用
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public void statusAndStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        // 修改员工账户信息
        employeeMapper.statusAndStop(employee);
    }

    /**
     * 修改员工
     * @param employeeDTO
     */
    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象copy
        BeanUtils.copyProperties(employeeDTO,employee);

        // 修改员工
        employeeMapper.statusAndStop(employee);
    }

    @Override
    public Employee getByIdEmp(Long id) {

        return employeeMapper.getByIdEmp(id);
    }
}
