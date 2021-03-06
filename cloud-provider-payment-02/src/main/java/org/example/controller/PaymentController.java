package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.CommonResult;
import org.example.entities.Payment;
import org.example.service.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author XZY
 * @version V1.0
 * @Package org.example.controller
 * @date 2020/6/21 14:54
 */
@RestController
@Slf4j
public class PaymentController {
    @Resource
    private PaymentServiceImpl paymentService;
    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/payment/create")
    private CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("插入结果："+result);
        if (result>0){
            return new CommonResult(200,"插入成功,server port: "+serverPort,result);
        }
        return new CommonResult(444,"插入失败",result);
    }

    @GetMapping(value = "/payment/get/{id}")
    private CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询结果："+payment);
        if (payment!=null){
            return new CommonResult<Payment>(200,"查询成功,server port:"+serverPort,payment);
        }
        return new CommonResult<Payment>(444,"查询失败,未找到数据"+id,null);
    }

    @GetMapping("/payment/lb")
    public String getPaymentLB(){
        return serverPort;
    }

    @GetMapping("/payment/feignTimeout")
    public String paymentFeignTimeout(){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

}
