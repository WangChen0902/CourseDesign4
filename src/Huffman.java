import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;

class HuffmanNode {
    //数据域
    private int data;
    //索引
    private int index;
    //左子节点
    private HuffmanNode left;
    //右子节点
    private HuffmanNode right;

    //哈夫曼节点的构造函数
    public HuffmanNode(int data,int index){
        this.data=data;
        this.index=index;
    }

    //私有属性的封装
    public int getData() {
        return data;
    }
    public void setData(int data) {
        this.data = data;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public HuffmanNode getLeft() {
        return left;
    }
    public void setLeft(HuffmanNode left) {
        this.left = left;
    }
    public HuffmanNode getRight() {
        return right;
    }
    public void setRight(HuffmanNode right) {
        this.right = right;
    }

}

public class Huffman {

    //压缩
    public int[] times = new int[256];
    public String[] HuffmanCodes = new String[256];
    public LinkedList<HuffmanNode> list = new LinkedList<HuffmanNode>();

    //解压
    //每个编码的长度
    public int [] codeLengths = new int [256];
    //对应的哈夫曼编码值
    public String [] codeMap=new String[256];

    //初始化
    public Huffman() {
        for (int i = 0; i < HuffmanCodes.length; i++) {
            HuffmanCodes[i] = "";
        }
    }

    //计算频数
    public void countTimes(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        int value;
        while ((value = fis.read())!= -1) {
            times[value]++;
        }
        fis.close();
//        for(int i : times) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
    }

    //构造哈夫曼树
    public HuffmanNode createTree() {
        //将次数作为权值构造森林
        for (int i = 0; i < times.length; i++) {
            if (times[i] != 0) {
                HuffmanNode node = new HuffmanNode(times[i], i);
                //将构造好的节点加入到容器中的正确位置
                list.add(getIndex(node), node);
                //System.out.println(times[i] + " " + i + " " + getIndex(node));
            }
        }

        //将森林（容器中的各个节点）构造成哈夫曼树
        while (list.size() > 1) {
            //获取容器中第一个元素（权值最小的节点）
            HuffmanNode firstNode = list.removeFirst();
            //获取中新的第一个元素，原来的第一个元素已经被移除了（权值次小的节点）
            HuffmanNode secondNode = list.removeFirst();
            //将权值最小的两个节点构造成父节点
            HuffmanNode fatherNode =
                    new HuffmanNode(firstNode.getData() + secondNode.getData(), -1);
            fatherNode.setLeft(firstNode);
            fatherNode.setRight(secondNode);
            //父节点加入到容器中的正确位置
            list.add(getIndex(fatherNode), fatherNode);
        }
        //返回整颗树的根节点
        return list.getFirst();
    }

    //利用前序遍历获取编码表
    public void getHuffmanCode(HuffmanNode root, String code) {
        //往左走，哈夫曼编码加0
        if (root.getLeft() != null) {
            getHuffmanCode(root.getLeft(), code + "0");
        }
        //往右走，哈夫曼编码加1
        if (root.getRight() != null) {
            getHuffmanCode(root.getRight(), code + "1");
        }
        //如果是叶子节点，返回该叶子节点的哈夫曼编码
        if (root.getLeft() == null && root.getRight() == null) {
			//System.out.println(root.getIndex()+"的编码为："+code);
            HuffmanCodes[root.getIndex()] = code;
        }
    }

    //压缩文件
    public void compress(String path, String destPath) throws Exception {


        //构建文件输出流
        FileOutputStream fos = new FileOutputStream(destPath);
        FileInputStream fis = new FileInputStream(path);


        /*===============把码表写入文件================*/
        //将整个哈夫曼编码以及每个编码的长度写入文件
        String code ="";
        for (int i = 0; i < 256; i++) {
            fos.write(HuffmanCodes[i].length());
            code+=HuffmanCodes[i];
            fos.flush();
        }
        //把哈夫曼编码写入文件

//		System.out.println("code="+code);
        String str1="";
        while(code.length()>=8){
            str1=code.substring(0, 8);
            int c=changeStringToInt(str1);
//			System.out.println(c);
            fos.write(c);
            fos.flush();
            code=code.substring(8);
        }
        //处理最后一个不为8的数
        int last = 8-code.length();
        for (int i = 0; i <last; i++) {
            code += "0";
        }
        str1=code.substring(0, 8);
        int c=changeStringToInt(str1);
        fos.write(c);
        fos.flush();

        /*===============将数据写入到文件中================*/

        //读文件，并将对应的哈夫曼编码串接成字符串
        int value = fis.read();
        String str = "";
        while (value != -1) {
            str += HuffmanCodes[value];
//			System.out.println((char)value+":"+str);
            value = fis.read();
        }
        //System.out.println(str);
        fis.close();

        String s = "";
        while (str.length() >= 8) {
            s = str.substring(0, 8);
            int b = changeStringToInt(s);
//				System.out.println(c);
            fos.write(b);
            fos.flush();
            str = str.substring(8);
        }

        int last1 = 8 - str.length();
        for (int i = 0; i < last1; i++) {
            str += "0";
        }
        s = str.substring(0, 8);
//			System.out.println(s);
        int d = changeStringToInt(s);
        fos.write(d);

        fos.write(last1);
        fos.flush();

        fos.close();

    }

    //插入元素位置的索引
    public int getIndex(HuffmanNode node) {
        for (int i = 0; i < list.size(); i++) {
            if (node.getData() <= list.get(i).getData()) {
                return i;
            }
        }
        return list.size();
    }

    //将字符串转换成整数
    public int changeStringToInt(String s) {
        int temp = 0;
        for(int i=0; i<8; i++) {
            temp += (s.charAt(i)- 48) * Math.pow(2, 7-i);
        }
//        System.out.println(s + " " + temp);
        return temp;
    }


    /*
     * 解压思路：
     * 1、读取文件里面的码表
     * 2、得到码表
     * 3、读取数据
     * 4、还原数据
     */

    public void decompress(String srcpath,String destpath) {

        try {
            FileInputStream fis = new FileInputStream(srcpath);
            FileOutputStream fos = new FileOutputStream(destpath);
            int value;
            int codeLength=0;
            String code="";
            //还原码表
            for (int i = 0; i < codeLengths.length; i++) {
                value=fis.read();
                codeLengths[i]=value;
//				System.out.println(times[i]);
                codeLength+=codeLengths[i];
            }

            //得到总长度
            //将总长度除以8的到字节个数
            int len=codeLength/8;
            //如果不是8的倍数，则字节个数加1（对应压缩补0的情况）
            if((codeLength)%8!=0){
                len++;
            }
            //读取哈夫曼编码
//			System.out.println("codeLength:"+len);
            for (int i = 0; i < len; i++) {
                //把读到的整数转换成二进制
                code += changeIntToString(fis.read());
            }
//			System.out.println("哈夫曼编码："+code);

            for (int i = 0; i < codeMap.length; i++) {
                //如果第i个位置不为0 ，则说明第i个位置存储有哈夫曼编码
                if(codeLengths[i]!=0){
                    //将得到的一串哈夫曼编码按照长度分割分割
                    String ss=code.substring(0, codeLengths[i]);
                    codeMap[i]=ss;
                    code=code.substring(codeLengths[i]);
                }else{
                    //为0则没有对应的哈夫曼编码
                    codeMap[i]="";
                }
            }

            //读取压缩的文件内容
            String codeContent="";
            while(fis.available()>1){
                codeContent+=changeIntToString(fis.read());
            }
            //读取最后一个
            value=fis.read();
            //把最后补的0给去掉
            codeContent=codeContent.substring(0, codeContent.length()-value);

            for (int i = 0; i < codeContent.length(); i++) {

                String codecontent=codeContent.substring(0, i+1);

                for (int j = 0; j < codeMap.length; j++) {
                    if(codeMap[j].equals(codecontent)){
//						System.out.println("截取的字符串："+codecontent);
                        fos.write(j);
                        fos.flush();
                        codeContent=codeContent.substring(i+1);
//						System.out.println("截取后剩余编码长度："+codeContent.length());
//						count=1;
                        i=-1;
                        break;
                    }
                }
            }
//			}

            fos.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //十进制转二进制字符串
    public String changeIntToString(int value) {
        String s="";
        for (int i = 0; i < 8; i++) {
            s=value%2+s;
            value=value/2;
        }
        return s;
    }

}

/*
class Test{
    public static void main(String args[]) throws Exception{

        //创建压缩对象
        Huffman compress = new Huffman();
        //统计文件中0-255出现的次数
        compress.countTimes("D:\\java\\test.txt");
        //构造哈夫曼树，并得到根节点
        HuffmanNode root=compress.createTree();
        //得到哈夫曼编码
        compress.getHuffmanCode(root, "");
        //压缩文件
        compress.compress("D:\\java\\test.txt",
                "D:\\java\\test_Huff.zip");
        compress.decompress("D:\\java\\test_Huff.zip", "D:\\java\\test_Huff.txt");
    }
}*/
