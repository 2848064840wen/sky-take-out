package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        // 判断账户的状态
        // TODO 判断账户状态，来是否登录成功
        if(employee.getStatus() == 1){
            // 启用状态
        }else{
            // 禁用状态
        }

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 添加员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加员工")
    public Result addEmp(@RequestBody EmployeeDTO employeeDTO){

        log.info("EmployeeDTO:    "  + employeeDTO);
        // 添加员工
        employeeService.addEmp(employeeDTO);

        return Result.success();
    }


    /**
     * 员工的分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> pagingSelect(EmployeePageQueryDTO employeePageQueryDTO){

        log.info("employeePageQueryDTO:   " + employeePageQueryDTO);
        PageResult pageResult = employeeService.pagingSelect(employeePageQueryDTO);


        return Result.success(pageResult);
    }


    /**
     * 账户的启动和禁用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("员工的账户的启用和禁用")
    public Result statusAndStop(@PathVariable("status") Integer status,Long id){

        log.info("账户状态: {}  账户id:  {}" , status,id);
        employeeService.statusAndStop(status,id);

        return Result.success();
    }


    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工")
    public Result<Employee> getByIdEmp(@PathVariable Long id){

        log.info("查询员工id为:  {}",id);
        Employee employee  = employeeService.getByIdEmp(id);

        return Result.success(employee);
    }

    /**
     * 修改员工信息
     * @return
     */
    @PutMapping
    @ApiOperation("修改员工信息")
    public Result updateEmp(@RequestBody EmployeeDTO employeeDTO){

        log.info("修改员工信息: {}",employeeDTO);
        employeeService.updateEmp(employeeDTO);

        return Result.success();
    }
}


