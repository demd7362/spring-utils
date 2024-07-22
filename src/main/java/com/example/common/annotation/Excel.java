package com.example.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {

    String table(); // 테이블명
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Unique { // 유니크인 필드에 다 달아주면 됨, insert 만 할거라서 auto increment인 pk는 고려 안함

    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Header {
        // 엑셀 템플릿에 포함시킬 필드에 붙여줘야함
        // 컬럼에서 non null인 필드는 다 붙여준다고 보면됨
        // non null인데 붙이지 않은 필드는 템플릿에서 값을 받지 않았으니 후처리가 필요함
        String name(); // 엑셀에서 표기될 헤더명
        int order(); // 헤더의 순서

        String regex() default "";

        String message() default "";


    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Processor { // 후처리용 어노테이션
        Class<?> value();
    }
}

