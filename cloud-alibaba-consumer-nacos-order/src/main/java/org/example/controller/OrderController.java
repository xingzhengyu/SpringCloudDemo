package org.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.example.entities.CommonResult;
import org.example.entities.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author XZY
 * @version V1.0
 * @Package org.example.controller
 * @date 2020/7/18 16:37
 */
@RestController
@Slf4j
public class OrderController {
    private static final String SERVICE_URL = "http://nacos-payment-provider";
    @Resource
    private RestTemplate restTemplate;

    @RequestMapping("/consumer/fallback/{id}")
    //@SentinelResource(value = "fallback") //没有配置
    //@SentinelResource(value = "fallback",fallback = "handlerFallback") //配置了fallback的，fallback只负责业务异常
    //@SentinelResource(value = "fallback",blockHandler = "blockHandler") // 配置了blockHandler，只负责sentinel控制台配置违规
    @SentinelResource(value = "fallback",fallback = "handlerFallback", blockHandler = "blockHandler",
            exceptionsToIgnore = {IllegalArgumentException.class}) // 配置了blockHandler和fallback
    public CommonResult<Payment> fallback(@PathVariable("id") Long id){
        CommonResult<Payment> commonResult = restTemplate.getForObject(SERVICE_URL + "/paymentSQL/" + id, CommonResult.class);
        if(id == 4){
            throw new IllegalArgumentException("IllegalArgumentException,非法参数异常");
        }else if(commonResult.getData() == null){
            throw new NullPointerException("NullPointerException,该ID没有记录，空指针异常");
        }
        return commonResult;
    }

    public CommonResult handlerFallback(Long id, Throwable e){
        Payment payment = new Payment(id, null);
        return new CommonResult(444, "兜底异常handler，exception内容"+e.getMessage(), payment);
    }

    public CommonResult blockHandler(Long id, BlockException exception){
        Payment payment = new Payment(id, null);
        return new CommonResult<>(445, "blockHandler-sentinel 限流，无此流水号：blockException" + exception.getMessage(), payment);
    }

}
