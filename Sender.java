import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.*;

public class Sender{
	public String file;
	public String ip;
	private final int allocation = 32;
	
	public static void main(String[] args) {
		Sender client = new Sender();
		client.getIp();
		SocketChannel socketChannel = client.createChannel();
		client.readFile();
		client.sendMetaData(socketChannel);
		client.sendFile(socketChannel);
	}
	
	private void getIp() {
		/*	Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the ip address of Receiver:");
		ip=scanner.nextLine();
		scanner.close();*/
		BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));
		try {
			ip=bReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(ip);
	}

	private void sendMetaData(SocketChannel socketChannel) {
		try {
			String fileName = (new File(file).getName());
			byte[] nameBytes = fileName.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(nameBytes);
			socketChannel.write(buffer);
			System.out.println("MetaData Sent..");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//private String size;

	private void readFile() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the complete path of file..");
		file=scanner.nextLine();
		scanner.close();
	}

	private void sendFile(SocketChannel socketChannel) {
		Path path = Paths.get(file);
		try {
			FileChannel incChannel = FileChannel.open(path);
			
			ByteBuffer buffer = ByteBuffer.allocate(1024*allocation);
			while(incChannel.read(buffer)>0){
				buffer.flip();
				socketChannel.write(buffer);
				buffer.clear();
			}
			//incChannel.close();
			socketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SocketChannel createChannel() {
		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(true);
			SocketAddress socketAddress = new InetSocketAddress(ip,5555);
			socketChannel.connect(socketAddress);
			return socketChannel;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}