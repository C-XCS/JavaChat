package qqServer.Utils;

import java.io.IOException; 
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author sky
 * @version 4.0
 * 	 定义对象输出流
 */
public class MyObjectOutputStream extends ObjectOutputStream{

	public MyObjectOutputStream(OutputStream out) throws IOException {
		super(out);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void writeStreamHeader() throws IOException {
		// TODO Auto-generated method stub
		return;
	}
	
}

/*
 *	当开聊天界面后 会单独启动一个socket 与 线程
 *		该线程接收服务端发送的信息包时, 报错:
 *  错误: java.io.StreamCorruptedException: invalid type code: AC
 *	位置: ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
 *	
 *
 *	原因:
 *	1.每当我们打开一个文件并尝试使用 ObjectOutputStream 和 FileOutputStream 将可序列化对象附加到文件末尾时，
 *	  ObjectOutputStream 都会将标头写入文件末尾以写入对象数据。
 *	2.每次打开文件并写入第一个对象时，ObjectOutputStream 都会在写入对象数据之前将标头写入文件末尾. 
 *	因此，以这种方式，每当以追加模式打开文件以使用 FileOutputStream 和 ObjectOutputStream 写入对象时, 标头都会被多次写入。
 *	
 *	解决:
 *	创建你自己的Object Output Stream类，
 *	比如MyObjectOutputStream类，通过扩展ObjectOutputStream（继承）和覆盖方法：“protected void writeStreamHeader（）抛出IOException。
 *	在新类中，此方法不应执行任何操作
 *
 *	详细参考: 
 *	https://www.geeksforgeeks.org/how-to-fix-java-io-streamcorruptedexception-invalid-type-code-in-java/
*/





