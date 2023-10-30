package space.caoshd.gencodemp.tool;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GeneToolsTest {

    @Test
    public void generateCode() {
        List<String> generateTables = Arrays.asList("t_file_export_instance");
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        dataSource.setURL("jdbc:mysql://localhost:3306/dbexpt?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
        new GeneTools(dataSource, "space.caoshd", "dbexpt", "cccc", generateTables).generate();
    }
}
