package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Repository.createFileplus;
import static gitlet.Utils.join;

public class blob implements Serializable {
String name;
byte[] content;
String id;
public blob(File file){
byte[] content = Utils.readContents(file);
name = file.getName();
id = getId();
}
void saveObject(){//把一个blob对象保存到对象文件夹中
    File dir = join(Repository.OBJECT_DIR, id.substring(0,2));
    if(!dir.exists()){
        dir.mkdir();
    }
    createFileplus(join(Repository.OBJECT_DIR, id.substring(0,2), id.substring(2,40)));
    Utils.writeObject(join(Repository.OBJECT_DIR, id.substring(0,2),id.substring(2,40)), this);
}
void savestage(){//把一个blob对象保存到暂存区文件夹中
    File file = join(Repository.STAGING_DIR, name);
    if(!file.exists()){
        createFileplus(file);
    }
    Utils.writeObject(file, this);
}
String getId(){
    return Utils.sha1(name, content);
 }
}
