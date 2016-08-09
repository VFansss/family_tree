package eu;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;


public class MyBatisCodeGeneratorApplication {

	public static final String GENERATOR_PATH_ONLINE = "src\\main\\resources\\generatorFiles";
	public static final String GENERATOR_PATH_BATCH ="src\\main\\resources\\generatorFilesBatch";
	
	public static void main(String[] args){
		
            File dir = new File(GENERATOR_PATH_ONLINE);
            for (File child : dir.listFiles()) {
                    if (child.isFile())
                            executeGenerator(child.getName(),GENERATOR_PATH_ONLINE);
            }
		
		

	}
	
	public static void executeGenerator(String generatorName, String workingPath) {
		
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;

            File configFile = new File(workingPath+"\\"+generatorName);
            File directory = new File("."); 
            System.out.println("Absolute path : " + directory.getAbsolutePath());
            System.out.println("Does the file exists ? " + configFile.exists());
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config;
            try {
                config = cp.parseConfiguration(configFile);
                DefaultShellCallback callback = new DefaultShellCallback(overwrite);
                MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
                myBatisGenerator.generate(null);

                if(warnings.size()!=0){
                        System.out.println("ATTENTION some warnings/errors founded : ");
                        for(int i=0;i<warnings.size();i++){
                                System.out.println(warnings.get(i));
                        }
                }else{
                        System.out.println("Execution successfully accomplished ...");
                }

            } catch (IOException e) {
                    e.printStackTrace();
            } catch (XMLParserException e) {
                    e.printStackTrace();
            } catch (SQLException e) {
                    e.printStackTrace();
            } catch (InterruptedException e) {
                    e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                    e.printStackTrace();
            }
		
	}
}
