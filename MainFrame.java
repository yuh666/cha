import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;
// welcome to mail to longcheng@58.com

public class MainFrame {

	// file root path
	private static final String ROOT_PATH = "./";
	// input file standard doc
	private static final String INPUT_FILE_STANDARD_TIEZI = ROOT_PATH + "input/standard_doc.txt";
	// input file check doc
	private static final String INPUT_FILE_CHECK_TIEZI = ROOT_PATH + "input/check_doc.txt";
	// output file check result
	private static final String OUTPUT_FILE_MY_RESULT = "./my_result.txt";
	// standard result file
	private static final String STANDARD_FILE_STANDARD_RESULT = ROOT_PATH + "output/standard_result.txt";
	private static final byte[] standardResult = new byte[65536 * 10];
	private static final byte[] userResult = new byte[65536 * 10]; 
	

	public static void main(String[] args) throws IOException {
		Check docChecker = new Check();
		
		//读取标准文档
		Vector <String> standarddocList = null;
		standarddocList = readStandarddoc();
		docChecker.init(standarddocList);
		
		//重复帖子检测
		Out<Long> totalTime = new Out<Long>(0L);
		totalTime.setValue(0L);
		int totalCheckCount = 0 ;
		totalCheckCount = readCheckdoc(docChecker, totalTime);

		//检测结果
		String result = checkResult(totalCheckCount, totalTime.getValue());
		System.out.println(result);
	}
	
	private static Vector<String> readStandarddoc() {
		File standardResultFile = new File(INPUT_FILE_STANDARD_TIEZI);
		Vector <String> readdocList = new Vector<String>();
		BufferedReader standardResultReader = null;
		try {
			InputStreamReader inputStreanm = new InputStreamReader(new FileInputStream(standardResultFile), "UTF-8");
			standardResultReader = new BufferedReader(inputStreanm);
			String tmpReaderBuf = null;
			while ((tmpReaderBuf = standardResultReader.readLine()) != null) {
				if(tmpReaderBuf.length() > 0){
					readdocList.add(tmpReaderBuf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (standardResultReader != null) {
				try {
					standardResultReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return readdocList;
	}
	/**
	 * 检测处理结果
	 * 
	 * @param totalTime 总的执行时间
	 * @return 检测结果:时间[空格]正确率
	 */
	private static String checkResult(int checkCount, long totalTime) {
		int standardCount = 0;
		// get standard result
		File standardResultFile = new File(STANDARD_FILE_STANDARD_RESULT);
		BufferedReader standardResultReader = null;
		try {
			standardResultReader = new BufferedReader(new FileReader(standardResultFile));
			String tmpReaderBuf = null;
			while ((tmpReaderBuf = standardResultReader.readLine()) != null) {
				//Integer tmpValue = Integer.parseInt(tmpReaderBuf);
				standardResult[standardCount] = Byte.parseByte(tmpReaderBuf);
				standardCount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (standardResultReader != null) {
				try {
					standardResultReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (checkCount != standardCount) {
			String reStr = "check count :" + checkCount +"--standardCount:"+ standardCount ;
			return reStr;
		}

		// write result
		File resultFile = new File(OUTPUT_FILE_MY_RESULT);
		BufferedWriter resultWriter = null;
		try {
			resultWriter = new BufferedWriter(new FileWriter(resultFile));
			for (int i = 0; i < checkCount; i++) {
				resultWriter.write(userResult[i] + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (resultWriter != null) {
				try {
					resultWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// calculate correct rate
		int totalYes = 0;
		double correctRate = 0.0f;
		for (int i = 0; i < checkCount; i++) {
			if (userResult[i] == standardResult[i]) {
				totalYes++;
			}
		}
		if(checkCount > 0){
			correctRate = (double)totalYes / checkCount;
		}
		// output
		return totalTime + " " + correctRate;
	}
	
	
	/**
	 * 读取文件并触发相关的操作
	 * @param outTime
	 * @return
	 */
	private static int readCheckdoc(Check checker, Out<Long> outTime) {
		// calculate time
		long totalTime = 0;
		long beginTime = 0;
		long endTime = 0;
		
		// check tiezi
		byte b = 0;
		int idx = 0;
		int charFlag = 0;
		int temp = 0;
		int totalCount = 0;
		int lineLen = 0;
		FileInputStream fis = null;
		FileChannel fc = null;
		char[] line = new char[10240];
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(10240);
		try {
			byteBuf.clear();
			fis = new FileInputStream(INPUT_FILE_CHECK_TIEZI);
			fc = fis.getChannel();
			while (fc.read(byteBuf) != -1) {
				byteBuf.flip();
				while (byteBuf.hasRemaining()) {
					if ((b = byteBuf.get()) == '\n') {
						//####################### check ##########################
						if(lineLen > 0){
							beginTime = System.nanoTime();
							temp = checker.check(line, lineLen);
							endTime = System.nanoTime();
							totalTime += ((endTime - beginTime) / 1000);
							userResult[totalCount] = (byte)temp;
							totalCount++;
						}
						idx = 0 ;
						lineLen = 0;
						charFlag = 0;
						//########################################################
					} else {
						if (charFlag == 21) {
							line[idx++] += b & 0x3f;
							charFlag = 0;
							lineLen++;
						} else if (charFlag == 31) {
							line[idx] += (char) ((char) (b & 0x3f) << 6);
							charFlag = 32;
						} else if (charFlag == 32) {
							line[idx++] += b & 0x3f;
							charFlag = 0;
							lineLen++;
						} else {
							if ((b >> 7) == 0) { // read one byte
								line[idx++] = (char) (b);
								lineLen++;
							} else if ((b >> 5) == (byte) 0xfe) { // read two  bytes
								line[idx] = (char) ((char) (b & 0x1f) << 6);
								charFlag = 21;
							} else if ((b >> 4) == (byte) 0xfe) { // read three bytes
								line[idx] = (char) ((char) (b & 0xf) << 12);
								charFlag = 31;
							}
						}
					}
				}
				byteBuf.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (fc != null) {
				try {
					fc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		outTime.setValue(totalTime);
		return totalCount;
	}
}

class Out<T> {
	private T value;
	
	public Out(T t) {
		value = t;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
