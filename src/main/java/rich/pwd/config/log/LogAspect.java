package rich.pwd.config.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
// 移除註解以裝配
// @Component
public class LogAspect {

  @Pointcut("execution(* rich.pwd.web.*.*(..))")
  private void logController() {
  }

  @Before(value = "logController()")
  private void logUrlB4(JoinPoint jp) {
    Logger log = LoggerFactory.getLogger(jp.getTarget().getClass().getName());
    HttpServletRequest servletReq =
            ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest();
    log.info("LogAspectInfo >>> Url: {} , Method: {} ", servletReq.getRequestURI(), jp.getSignature().getName());
  }

  @After(value = "logController()")
  private void logMemoryAfter(JoinPoint jp) {
    Logger log = LoggerFactory.getLogger(jp.getTarget().getClass().getName());
    log.info("LogAspectInfo >>> Memory Usage : {} M", Runtime.getRuntime().freeMemory() / 1024 / 1024);
  }

  // @AfterReturning(value = "logController()",returning = "rtnObj")
  // private void logRtnListSize(JoinPoint jp, Object rtnObj){
  //   ResponseEntity resEntity = (ResponseEntity) rtnObj;
  //   Object obj = resEntity.getBody();
  //   if(obj instanceof Collection<?>){
  //     List<Object> objList = (List<Object>) obj;
  //     Logger log = LoggerFactory.getLogger(jp.getTarget().getClass().getName());
  //     log.info("LogAspectInfo >>> 查詢筆數 :{}", objList.size());
  //   }
  // }

  @Around(value = "logController()")
  private Object logUsedTime(ProceedingJoinPoint pjp) throws Throwable {
    long startTime = System.currentTimeMillis();
    Logger log = LoggerFactory.getLogger(pjp.getTarget().getClass().getName());
    Object[] args = pjp.getArgs();
    Object rtn = pjp.proceed(args);
    long finishTime = System.currentTimeMillis();
    log.info("LogAspectInfo >>> 執行耗費 (Milliseconds) :{}", (finishTime - startTime));
    return rtn;
  }

  @AfterThrowing(value = "logController()", throwing = "ex")
  private void logException(JoinPoint jp, Exception ex) {
    Logger log = LoggerFactory.getLogger(jp.getTarget().getClass().getName());
    log.error("LogAspectInfo >>> 錯誤 Msg :{}", ex.getMessage());
  }
}
