import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: mybatis代码生成
 * @author: Zhaotianyi
 * @time: 2021/5/6 10:51
 */
public class Gen {

    public void generator() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        /**指向逆向工程配置文件*/
        String PROJECT_PATH = System.getProperty("user.dir");
        File configFile = new File(PROJECT_PATH+"/src/Test/resources/generatorConfig.xml");
        ConfigurationParser parser = new ConfigurationParser(warnings);
        Configuration config = parser.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);
    }
    public static void main(String[] args) {
        try {
            Gen codeGenerator = new Gen();
            codeGenerator.generator();
            System.out.println("生成成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
