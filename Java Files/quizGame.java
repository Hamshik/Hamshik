import java.util.*;
import java.io.*;
public class quizGame{
	public static void main(String[] args){
		ArrayList<String> ques = new ArrayList<>();
		ArrayList<String> ans = new ArrayList<>();
		ArrayList<String> choice = new ArrayList<>();
		String quesPath = "src/question.txt";
		String ansPath = "src/answer.txt";
		String choiPath = "src/choice.txt";
		String line;
		try(BufferedReader quesReader = new BufferedReader(new FileReader(quesPath))){
			while((quesReader.read() = line) != null ){
				ques.add(line);
			}
		}
		catch(FileNotFoundException){
			System.out.println("File didnot found!!")
		}
		try(BufferedReader ansReader = new BufferedReader(new FileReader(ansPath))){
			while((quesReader.read() = line) != null ){
				ques.add(line);
			}
		}
		catch(FileNotFoundException){
			System.out.println("File didnot found!!")
		}
		try(BufferedReader choiReader = new BufferedReader(new FileReader(choiPath))){
			while((quesReader.read() = line) != null ){
				ques.add(line);
			}
		catch(FileNotFoundException){
			System.out.println("File didnot found!!")
		}
		}
	}
}