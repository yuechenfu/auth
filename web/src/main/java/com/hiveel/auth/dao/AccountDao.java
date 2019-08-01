package com.hiveel.auth.dao;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AccountDao {
    @Insert("insert into account(username, password, personId,   extra, createAt, updateAt) values(#{username}, #{password}, #{personId},  #{extra}, #{createAt}, #{updateAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(Account e);

    @Delete("delete from account where id=#{id}")
    int delete(Account e);

    @Delete("delete from account where personId=#{personId}")
    int deleteByPersonId(Account e);

    @UpdateProvider(type = Sql.class, method = "update")
    int update(Account e);

    @Update("update account set password=#{password}, updateAt=#{updateAt} where id=#{id}")
    int updatePassword(Account e);

    @Select("select * from account e where id=#{id}")
    Account findById(Account e);

    @SelectProvider(type = Sql.class, method = "findByPersonId")
    List<Account> findByPersonId(@Param("searchCondition") SearchCondition searchCondition, @Param("account") Account account);

    @Select("select count(*) from account e where username=#{username}")
    int countByUsername(Account e);

    @Select("select * from account e where username=#{username} ")
    Account findByUsername(Account e);

    @Select("select * from account e where username=#{username} and password=#{password}")
    Account findByUsernameAndPassword(Account e);

    class Sql {
        public static String update(final Account e) {
            return new SQL() {{
                UPDATE("account");
                if (e.getPersonId() != null) SET("personId=#{personId}");
                if (e.getUsername() != null) SET("username=#{username}");
                if (e.getPassword() != null) SET("password=#{password}");
                if (e.getMg() != null) SET("mg=#{mg}");
                if (e.getExtra() != null) SET("extra=#{extra}");
                SET("updateAt=#{updateAt}");
                WHERE("id=#{id}");
            }}.toString();
        }

        public static String findByPersonId(@Param("searchCondition") final SearchCondition searchCondition, @Param("account") Account account) {
            return new SQL() {{
                SELECT("*");
                FROM("account e");
                WHERE("e.personId = #{account.personId}");
            }}.toString().concat(searchCondition.getSqlOrder()).concat(searchCondition.getSqlLimit());
        }
    }
}

