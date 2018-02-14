package jBPM64Ex041_255char_variable_mysql;


public class Tmp {

    public static void main(String[] args) {
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            sb.append("a");
        }
        sb.append("xxxxx");
        
        System.out.println(sb.toString());
        
    }
}
