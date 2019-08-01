package com.hiveel.auth.dao;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Forget;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ForgetDao {
    @Insert("insert into forget(accountId, code, createAt, updateAt) values( #{account.id},  #{code},  #{createAt}, #{updateAt} )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Forget e);

    @Delete("delete from forget where id=#{id}")
    int delete(Forget e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Forget e);

    @Select("select * from forget where id=#{id}")
    @Results({@Result(property = "account.id", column = "accountId")})
    Forget findById(Forget e);

    @SelectProvider(type = Sql.class, method = "count")
    int count(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "find")
    @Results({@Result(property = "account.id", column = "accountId")})
    List<Forget> find(@Param("searchCondition") SearchCondition searchCondition);

    @SelectProvider(type = Sql.class, method = "countByAccountId")
    int countByAccountId(@Param("searchCondition") SearchCondition searchCondition, @Param("forget") Forget e);

    @SelectProvider(type = Sql.class, method = "findByAccountId")
    @Results({@Result(property = "account.id", column = "accountId")})
    List<Forget> findByAccountId(@Param("searchCondition") SearchCondition searchCondition, @Param("forget") Forget e);

    @Select("select * from forget where accountId=#{account.id} and code=#{code} order by id desc limit 1")
    @Results({@Result(property = "account.id", column = "accountId")})
    Forget findByAccountIdAndCode(Forget e);

    class Sql {
        public static String update(final Forget e) {
            return new SQL() {{
                UPDATE("forget");
                if (e.getAccount() != null && e.getAccount().getId()!=null) SET("accountId=#{account.id}");
                if (e.getCode() != null) SET("code=#{code}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String count(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("count(*)"); FROM("forget e");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String find(@Param("searchCondition") final SearchCondition searchCondition) {
            return new SQL() {{
                SELECT("*"); FROM("forget e");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }

        public static String countByAccountId(@Param("searchCondition") final SearchCondition searchCondition, @Param("forget") final  Forget e) {
            return new SQL() {{
                SELECT("count(*)"); FROM("forget e");
                WHERE("accountId = #{forget.account.id}");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString();
        }

        public static String findByAccountId(@Param("searchCondition") final SearchCondition searchCondition, @Param("forget") final  Forget e) {
            return new SQL() {{
                SELECT("*"); FROM("forget e");
                WHERE("accountId = #{forget.account.id}");
                if (searchCondition.getMinDate() != null) WHERE("createAt >= #{searchCondition.minDate}");
                if (searchCondition.getMaxDate() != null) WHERE("createAt <= #{searchCondition.maxDate}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}
