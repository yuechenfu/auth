package com.hiveel.auth.dao;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.LoginRecord;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface LoginRecordDao {
    @Insert("insert into loginRecord(personId, ip, device, updateAt) values( #{personId}, #{ip}, #{device}, #{updateAt} )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(LoginRecord e);

    @Delete("delete from loginRecord where id=#{id}")
    int delete(LoginRecord e);

    @Select("select * from loginRecord where id=#{id}")
    LoginRecord findById(LoginRecord e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "find")
    List<LoginRecord> find(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "countByPersonId")
    int countByPersonId(@Param("searchCondition") SearchCondition searchCondition, @Param("loginRecord") LoginRecord e);

    @SelectProvider(type = Sql.class, method = "findByPersonId")
    List<LoginRecord> findByPersonId(@Param("searchCondition") SearchCondition searchCondition, @Param("loginRecord") LoginRecord e);

    class Sql {
        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("loginRecord e");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("loginRecord e");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }

        public static String countByPersonId(@Param("searchCondition") final SearchCondition searchCondition, @Param("loginRecord") final LoginRecord e) {
            return new SQL() {{
                SELECT("count(*)"); FROM("loginRecord e");
                WHERE("personId=#{loginRecord.personId}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String findByPersonId(@Param("searchCondition") final SearchCondition searchCondition, @Param("loginRecord") final LoginRecord e) {
            return new SQL() {{
                SELECT("*"); FROM("loginRecord e");
                WHERE("personId=#{loginRecord.personId}");
                if (searchCondition.getMinDate() != null) WHERE("updateAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("updateAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
