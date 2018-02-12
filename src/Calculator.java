import java.util.Scanner;
import java.util.*;
import java.math.*;

/**
 * Created by Samsung on 12.02.2018.
 */
public class Calculator {

    public static boolean isValidExpression(String expression) {

        if ((!Character.isDigit(expression.charAt(0)) && !(expression.charAt(0) == '(')  && !(expression.charAt(0) == '-'))
                || (!Character.isDigit(expression.charAt(expression.length() - 1)) && !(expression.charAt(expression.length() - 1) == ')'))) {
            return false;
        }

        HashSet<Character> validCharactersSet = new HashSet<>();
        validCharactersSet.add('*');
        validCharactersSet.add('+');
        validCharactersSet.add('-');
        validCharactersSet.add('/');
        validCharactersSet.add('^');
        validCharactersSet.add('(');
        validCharactersSet.add(')');
        validCharactersSet.add(' ');

        Stack<Character> validParenthesisCheck = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {

            if (!Character.isDigit(expression.charAt(i)) && !validCharactersSet.contains(expression.charAt(i))) {
                return false;
            }

            if (expression.charAt(i) == '(') {
                validParenthesisCheck.push(expression.charAt(i));
            }

            if (expression.charAt(i) == ')') {

                if (validParenthesisCheck.isEmpty()) {
                    return false;
                }
                validParenthesisCheck.pop();
            }
        }

        if (validParenthesisCheck.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkOperator(String s,int pos)
    {
        boolean checked;
        while(true)
        {
            if(Character.isDigit(s.charAt(pos-1)))
            {
                checked=false;
                break;
            }
            if(isOperator(s.charAt(pos-1)))
            {
                checked=true;
                break;
            }
            pos--;
        }
        return checked;
    }


    public static void performArithmeticOperation(Stack<BigDecimal> operandStack, Stack<Character> operatorStack)throws Exception{
        try {
            BigDecimal value1 = operandStack.pop();//выгружаем 2 операнда
            BigDecimal value2 = operandStack.pop();
            char operator = operatorStack.pop();//выгружаем оператор

            BigDecimal intermediateResult = arithmeticOperation(value1, value2, operator);

            operandStack.push(intermediateResult);

        } catch (EmptyStackException e) {
            throw e;
        }
    }


    public static String replaceCharAt(String s, int pos, char c) {

        return s.substring(0,pos) + c + s.substring(pos+1);

    }
    static boolean isOperator(char c)
    {
        switch(c)
        {
            case '(':return true;
            case ')':return true;
            case '+':return true;
            case '-':return true;
            case '*':return true;
            case '/':return true;
            case '^':return true;

        }
        return false;

    }
    static int checkPrecedence(Character c)
    {
        switch(c)
        {
            case '(':return 0;
            case ')':return 0;
            case '+':return 1;
            case '-':return 1;
            case '*':return 2;
            case '/':return 2;
            case '^':return 3;
            default:return -1;

        }

    }
    static long calculateExpression(String expression) throws Exception
    {
        Stack<BigDecimal> operands= new Stack<>();//стэк операндов
        Stack<Character> operators= new Stack<>();//стек операторов

        String number ="";
        boolean isNumber=false;

        for(int i=0;i<expression.length();i++)
        {
            if(expression.charAt(i)==' ')
                continue;
            if((expression.charAt(i)=='-'&&i==0)||
                    (expression.charAt(i)=='-'&&(isOperator(expression.charAt(i-1))||checkOperator(expression,i))))//проверяем является ли минус оператором
            {
                number=expression.charAt(i)+"";
                isNumber=true;
                continue;
            }
            if(Character.isDigit(expression.charAt(i))||expression.charAt(i)==',')//если число
            {
                if(!isNumber)
                {
                    number=expression.charAt(i)+"";
                    isNumber=true;
                }
                else
                {
                    number+=expression.charAt(i);
                }
                if(i==expression.length()-1 || (!Character.isDigit(expression.charAt(i+1))&&expression.charAt(i+1)!=','))//если конец искомого операнда
                {
                    operands.push(new BigDecimal(new Scanner(number).nextDouble()).setScale(10,BigDecimal.ROUND_HALF_UP));
                    isNumber=false;
                    number="";
                }
            }
            if(isOperator(expression.charAt(i)))//если оператор
            {
                if(expression.charAt(i)==')') {//если закрывающая скобка высвобождаем стек до открывающей
                    while (operators.peek()!='(')
                    {
                        performArithmeticOperation(operands,operators);//производим вычисление
                    }
                    operators.pop();

                }
                else
                {
                    while(!operators.isEmpty()&&checkPrecedence(operators.peek())>checkPrecedence(expression.charAt(i)))
                    {
                        performArithmeticOperation(operands,operators);

                    }
                    operators.push(expression.charAt(i));

                }
            }
        }
        while(!operators.isEmpty())
        {
            performArithmeticOperation(operands,operators);
        }

        System.out.println(operands.peek());

        return 0;
    }

    public static BigDecimal arithmeticOperation(BigDecimal value2, BigDecimal value1, Character operator) {

        BigDecimal result;

        switch (operator) {

            case '+':
                result = value1.add(value2);
                break;

            case '-':
                result = value1.subtract(value2);
                break;

            case '*':
                result = value1.multiply(value2);
                break;

            case '/':
                if(new Scanner(value2.toString()).nextDouble()==0)
                {
                    throw new ArithmeticException(); // System.exit(0);
                }
                result=value1.divide(value2);
                break;

            case '^':
                int pow=new Scanner(value2.setScale(0).toString()).nextInt();
                if(pow>=0)
                    result = value1.pow(pow);
                else
                {
                    pow*=-1;
                    value1=new BigDecimal(1).divide(value1);
                    result=value1.pow(pow);
                }
                break;

            default:
                result = value1.add(value2);


        }

        return result;
    }
    public static void main(String args[])  {
        while(true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Введите выражение:");
            String expression = in.nextLine();
            expression.replace('.',',');//заменяем все точки на запятые

            try {
                if(!isValidExpression(expression))//если строка неалидная
                    throw new Exception();
                calculateExpression(expression);//обрабатываем выражение
            }
            catch (ArithmeticException e) {
                System.out.println("Делить на ноль нельзя!!!");
            }
            catch (Exception e) {
                System.out.println("невалидное выражение!!!!");
            }


        }
    }
}
