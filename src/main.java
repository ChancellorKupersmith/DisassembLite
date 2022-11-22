import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class main {

    final static int R_TYPE = 0, I_TYPE = 1, B_TYPE = 2, CB_TYPE = 3, D_TYPE = 4, SPECIAL_TYPE = 5;
    static final String[] opcodes = {
            /*R ADD*/"10001011000",
            //00010 0000000000 100000
            /*R AND*/"10001010000",
            /*R BR*/"11010110000",
            /*R EOR*/"11001010000",
            /*R LSL*/"11010011011",
            /*R LSR*/"11010011010",
            /*R ORR*/"10101010000",
            /*R SUB*/"11001011000",
            /*R SUBS*/"11101011000",
            /*R MUL*/"10011011000",
            /*Special PRNT*/"11111111101",
            /*Special PRNL*/"11111111100",
            /*Special DUMP*/"11111111110",
            /*Special HALT*/"11111111111",

            /*I ADDI*/"1001000100",
            /*I ANDI*/"1001001000",
            /*I EORI*/"1101001000",
            /*I ORRI*/"1011001000",
            /*I SUBI*/"1101000100",
            /*I SUBIS*/"1111000100",

            /*B*/"000101",
            /*B BL*/"100101",

            /*HERE CB B.cond*/"1001000100",
            /*CB CBZ*/"10110100",

            /*D LDUR*/"11111000010",
            /*D STUR*/"11111000000",
    };
    static final String instructions[] = {
            "ADD ",
            "AND ",
            "BR ",
            "EOR ",
            "LSL ",
            "LSR ",
            "ORR ",
            "SUB ",
            "SUBS ",
            "MUL ",
            "PRNT ",
            "PRNL ",
            "DUMP ",
            "HALT ",

            "ADDI ",
            "ANDI ",
            "EORI ",
            "ORRI ",
            "SUBI ",
            "SUBIS ",

            "B ",
            "BL ",

            /*HERE CB B.cond*/"B.",
            "CBZ ",

            "LDUR ",
            "STUR ",
    };
    public static void main(String[] args) throws IOException {
        String output = "";
        ArrayList<String> binaryInstructs = new ArrayList<>();
        HashMap<Character, String> hexToBin = new HashMap<Character, String>();

        // init hex bin key value pairs
        hexToBin.put('0', "0000");
        hexToBin.put('1', "0001");
        hexToBin.put('2', "0010");
        hexToBin.put('3', "0011");
        hexToBin.put('4', "0100");
        hexToBin.put('5', "0101");
        hexToBin.put('6', "0110");
        hexToBin.put('7', "0111");
        hexToBin.put('8', "1000");
        hexToBin.put('9', "1001");
        hexToBin.put('a', "1010");
        hexToBin.put('b', "1011");
        hexToBin.put('c', "1100");
        hexToBin.put('d', "1101");
        hexToBin.put('e', "1110");
        hexToBin.put('f', "1111");
    //read in binary file
        if(args.length == 1){
            try{
                InputStream is = new FileInputStream(args[0]);
                int firstByte;
                String bIntsruct = "";
                //while loop until reaches end of input file
                while((firstByte = is.read()) != -1){
                    bIntsruct += hexToBin.get((char) firstByte);
                    for (int i = 0; i < 3; i++) {
                        int hex = is.read();
                        bIntsruct += hexToBin.get((char) hex);;
                    }
                    //skip white space
                    is.read();
                    if(bIntsruct.length() == 32){
//                        System.out.println(bIntsruct.length());
                        binaryInstructs.add(bIntsruct);
                        bIntsruct = "";
                    }
                }
        //Parse instructions and add results to output string
//                System.out.println(binaryInstructs.size());
                for (String instruction : binaryInstructs) {
                    String opcode = "";
                    boolean instructionParsed = false;
                    for (int i = 0; i < instruction.length(); i++) {
                        //check if opcode is a valid instruction
                        int opcodeIndex = isOpcode(opcode);
                        if(opcodeIndex != -1){
                            if(instructionParsed){
                                break;
                            }
                            int type = getType(opcodeIndex);
                            switch (type){
                                case R_TYPE:
                                    output = parseRType(instruction, opcodeIndex, output);
                                    instructionParsed = true;
                                    break;
                                case I_TYPE:
                                    output = parseIType(instruction, opcodeIndex, output);
                                    instructionParsed = true;
                                    break;
                                case B_TYPE:
                                    output = parseBType(instruction, opcodeIndex, output);
                                    instructionParsed = true;
                                    break;
                                case CB_TYPE:
                                    output = parseCBType(instruction, opcodeIndex, output);
                                    instructionParsed = true;
                                    break;
                                case D_TYPE:
                                    output = parseDType(instruction, opcodeIndex, output);
                                    instructionParsed = true;
                                    break;
                                default:
                            }
                        }
                        else{

                            opcode += instruction.charAt(i);
                        }
                    }

                }

            }catch (IOException e){
                System.out.println("Error reading input file");
                throw e;
            }
        }
        else{
            throw new IllegalArgumentException("Invalid argument given. Expected 1 input machine file as argument");
        }
        //parse binary instruction

        //output
        System.out.println(output);
    }

    //search for opcode
    static int isOpcode(String str){
        if(str.length() > 5){
            int index = 0;
            for (String opcode : opcodes) {
                if(str.equalsIgnoreCase(opcode)){
                    return index;
                }
                index++;
            }
        }
        return -1;
    }
    //get Instruction type
    static int getType(int index){
        if(index < 14){
            return R_TYPE;
        }
        else if(index < 20){
            return I_TYPE;
        }
        else if(index < 22){
            return B_TYPE;
        }
        else if(index < 24){
            return CB_TYPE;
        }
        else if(index < 26){
            return D_TYPE;
        }
        return -1;
    }
    public static String parseRType(String binaryInstruct, int index, String output){
        String instruction = instructions[index];
        String rm = "X" + Integer.parseInt(binaryInstruct.substring(11, 16), 2);
        String shamt = " #" + Integer.parseInt(binaryInstruct.substring(16, 22), 2);
        String rn = "X" + Integer.parseInt(binaryInstruct.substring(22, 27), 2);
        String rd = "X" + Integer.parseInt(binaryInstruct.substring(27, 32), 2);
        if(instruction == "LSL " || instruction == "LSR "){
            output += binaryInstruct + " " + instruction + rd + ", " + rn + shamt +"\n";
        }
        else if(instruction == "BR "){
            output += binaryInstruct + " " + instruction + rd + "\n";
        }
        else if(instruction == "HALT "){
            output += binaryInstruct + " " + instruction + "\n";
        }
        else if(instruction == "DUMP "){
            output += binaryInstruct + " " + instruction + "\n";
        }
        else if(instruction == "PRNL "){
            output += binaryInstruct + " " + instruction + "\n";
        }
        else if(instruction == "PRNT "){
            output += binaryInstruct + " " + instruction + rd + "\n";
        }
        else{
            output += binaryInstruct + " " + instruction + rd + ", " + rn + ", " + rm +"\n";
        }
        return output;
    }
    public static String parseIType(String binaryInstruct, int index, String output){
        String instruction = instructions[index];
        String i = "#" + Integer.parseInt(binaryInstruct.substring(11, 19), 2);
        String rn = "X" + Integer.parseInt(binaryInstruct.substring(22, 27), 2);
        String rd = "X" + Integer.parseInt(binaryInstruct.substring(27), 2);
        output += binaryInstruct + " " + instruction + rd + ", " + rn + ", " + i + "\n";
        return output;
    }
    public static String parseBType(String binaryInstruct, int index, String output){
        String instruction = instructions[index];
        int brAddress = Integer.parseInt(binaryInstruct.substring(5), 2);
        output += binaryInstruct + " " + instruction + brAddress + "\n";
        return output;
    }
    public static String parseCBType(String binaryInstruct, int index, String output){
        String instruction = instructions[index];
        int brAddress = Integer.parseInt(binaryInstruct.substring(7,27), 2);
        String rt = "X" + Integer.parseInt(binaryInstruct.substring(27), 2);
        if(instruction == "B."){
            output += binaryInstruct + " " + instruction + " !!DEBUG!!\n";
        }else{
            output += binaryInstruct + " " + instruction + rt + ", " + brAddress + "\n";
        }
        return output;
    }
    public static String parseDType(String binaryInstruct, int index, String output){
        String instruction = instructions[index];
        String dtAddress = "X" + Integer.parseInt(binaryInstruct.substring(11, 20), 2);
        String op = " #" + Integer.parseInt(binaryInstruct.substring(20, 22), 2);
        String rn = "X" + Integer.parseInt(binaryInstruct.substring(22, 27), 2);
        String rt = "X" + Integer.parseInt(binaryInstruct.substring(27, 32), 2);
        output += binaryInstruct + " " + instruction + dtAddress + ", [" + rn + ", #" + rt + "]\n";
        return output;
    }
}


