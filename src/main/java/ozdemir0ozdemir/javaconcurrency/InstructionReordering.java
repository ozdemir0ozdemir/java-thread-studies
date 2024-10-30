package ozdemir0ozdemir.javaconcurrency;

public class InstructionReordering {

    public static void calc() {
        int a;
        int b = 2;
        int c = 3;
        int d;
        int e = 5;
        int f;
        int g = 7;
        int h = 8;
        int i;
        int j = 10;

        a = b + c;
        d = a + e;

        f = g + h;
        i = f + j;

        int total = a + b + c + d + e +f +g +h + i + j;
        System.out.println(total);
    }


    public static void main(String[] args) {
        calc();



    }
}
