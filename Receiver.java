import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class Receiver{
	
	private final int allocation = 1024;
	
	public static void main(String[] args) {
		Receiver receiver = new Receiver();
		SocketChannel socketChannel = receiver.createSocketChannel();
		receiver.getFileName(socketChannel);
		receiver.readFileFromServer(socketChannel);
	}

	private void getFileName(SocketChannel socketChannel) {
				try {
					ByteBuffer nameBuffer = ByteBuffer.allocate(256);
					socketChannel.read(nameBuffer);
					int position = nameBuffer.position();
					nameBuffer.rewind();
					file="";
					int startPosition = 0;
					
					StringBuilder sb = new StringBuilder();
					System.out.println(" Receiving position: "+position);
					
					while(startPosition<position){
						sb.append((char)nameBuffer.get());
						startPosition++;
					}
					file=sb.toString();
					System.out.println("MetaData Received");
					System.out.println("File: "+file);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	private String file;
	//private String size;

	private void readFileFromServer(SocketChannel socketChannel) {
		Path path = Paths.get(file);
		try {
			FileChannel fileChannel = FileChannel.open(path, 
					EnumSet.of(StandardOpenOption.CREATE,
							StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.WRITE)
					);
			ByteBuffer buffer = ByteBuffer.allocate(1024*allocation);
			while(socketChannel.read(buffer)>0){
				buffer.flip();
				fileChannel.write(buffer);
				buffer.clear();
			}
			fileChannel.close();
			System.out.println("File Received..");
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SocketChannel createSocketChannel() {
		ServerSocketChannel serverSocket = null;
		SocketChannel client = null;
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.configureBlocking(true);
			serverSocket.bind(new InetSocketAddress(5555));
			System.out.println("Receiver running");
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
			client=serverSocket.accept();
			System.out.println("Connection Established.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client;
	}
}