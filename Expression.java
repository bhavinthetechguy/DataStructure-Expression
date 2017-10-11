package apps;

import java.io.*;
import java.util.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays; 
	
	ArrayList<Integer> openingBracketIndex; 
	ArrayList<Integer> closingBracketIndex;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
        scalars = null;
        arrays = null;
        
        openingBracketIndex = null;
        closingBracketIndex = null;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
   
    	
   /* private StringTokenizer bracketS(String expression)
    { 
		String token = "";
		StringTokenizer st = new StringTokenizer(expression,  " *+-/()");
		
		while (st.hasMoreElements())
		{
			String value = st.nextToken();
			int openArrayBracket = value.indexOf('[');
			int closeArrayBracket = value.indexOf(']');
			
			if (openArrayBracket == -1 && closeArrayBracket ==-1) 
			{
				
				token += value + " ";
			}
			else if(closeArrayBracket != -1 && openArrayBracket==-1)
			{
				
				token += value.length()>1? value.substring(0,closeArrayBracket) + " " : "";
			}
			else
			{ 		
				token += value.length()== value.substring(0,openArrayBracket).length()+1?  value.substring(0, openArrayBracket+1)+"] ":
				value.substring(0, openArrayBracket+1) + arraySaperator(value.substring(openArrayBracket+1));
			}		
		}
		st = new StringTokenizer(token," ");
		
		return st;
	}
    
   
*/
   
    public void buildSymbols() 

    {
        StringTokenizer st = new StringTokenizer(expr, delims, true);       
    	scalars = new ArrayList <ScalarSymbol>();
    	arrays = new ArrayList <ArraySymbol>();
    	
        Stack <String> brackets = new Stack <String>();
        String token = "";
        
        while (st.hasMoreTokens())
        {
	        token = st.nextToken();
/*
 *if (openArrayBracket == -1 && closeArrayBracket ==-1) 
			{
				
				token += value + " ";
			}
			else if(closeArrayBracket != -1 && openArrayBracket==-1)
			{
				
				token += value.length()>1? value.substring(0,closeArrayBracket) + " " : "";
			}
 */
	        if ((token.charAt(0) >= 'a' && token.charAt(0) <= 'z') ||
	        		(token.charAt(0) >= 'A' && token.charAt(0) <= 'Z' || 
	        		token.equals("[")))

	        	brackets.push(token);	        
        }

        while(!brackets.isEmpty())
        {
	        token = brackets.pop();

	        if (token.equals("["))
	        {
	            token = brackets.pop();
	            ArraySymbol aBrackets = new ArraySymbol(token);
	            if(arrays.indexOf(aBrackets) == -1)
	            arrays.add(aBrackets);
	        }
	        else 
	        {
	            ScalarSymbol sBrackets = new ScalarSymbol(token);
	            if (scalars.indexOf(sBrackets) == -1)
	            	scalars.add(sBrackets);
            }
        }
    }
 /*  
    	String expression = expr;
    	StringTokenizer st= bracketS(expression);
    	scalars = new ArrayList<ScalarSymbol>();
    	arrays= new ArrayList<ArraySymbol>();
	
    	while(st.hasMoreTokens())
    	{
    		String value = st.nextToken();
    		int hasArray = (value.indexOf('['));
    		boolean hasDigit = Character.isDigit(value.charAt(0));
		
    		if(hasArray<0 && !(hasDigit))
    		{
			ScalarSymbol create = new ScalarSymbol(value);
			scalars.add(create);
    		}
    		
    		else if (!(hasDigit))
    		{
			ArraySymbol arrayValue = new ArraySymbol(value.substring(0,hasArray));
			arrays.add(arrayValue);
    		}
    	}
	
    }*/
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public boolean isLegallyMatched() 
    {
    	
        Stack <Character> openBrack = new Stack <Character>();
        Stack <Integer> position = new Stack <Integer>();
        openingBracketIndex = new ArrayList <Integer>();
        closingBracketIndex = new ArrayList <Integer>();
        
        int length = expr.length();
        int num = 0, count = 0;
        

        for (int a = 0; a < length; a++)
        {
              if(expr.charAt(a) == ')' || expr.charAt(a) == ']')

                 num++;
        }
        for (int b = 0; b <= num; b++)
        {
             closingBracketIndex.add(0);
        }
        
        for (int i = 0; i < length; i++)
        {
	        if (expr.charAt(i) == '(' || expr.charAt(i) == '[')
	        {
	        		openingBracketIndex.add(i);    
	        		openBrack.push(expr.charAt(i));
	        		position.push(count);
	                count++;
            }
            else if (expr.charAt(i) == ')' || expr.charAt(i) == ']')
            {
                if (openBrack.isEmpty())
                {
                        return false;
                }
                else 
                {
                	char ch = openBrack.pop();
                    if (expr.charAt(i) == ')' && ch != '(' 
                    		|| expr.charAt(i) == ']'
                    		&& ch != '[')       
                    	return false;        
                    closingBracketIndex.set(position.pop(), i);
                }
            }
        }

        		if (!openBrack.isEmpty())
        			return false;
        
        		return true;

    }
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
 
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    private String findArrValue(String name, int index)
    {
	
		for (int i=0 ; i<arrays.size(); i++)
		{			
			ArraySymbol check = arrays.get(i);		
			if (name.equals(check.name))
			{	
				String value2 =  String.valueOf(check.values[index]);
				return value2;
			}
		}
		return "0";
    }

    private String findScalValue(String name)
    {
    	for (int i=0 ; i<scalars.size();i++)
    	{
    		ScalarSymbol check=	scalars.get(i);
		
    		if (check.name.equals(name))
    		{
    			String value3 =String.valueOf(check.value);
    			return  value3;
    		}
	
    	}
    	return "0";
    }	
    /*
     * String topOp = operators.pop();

        float temp1 = 0;

        float temp2 = 0;

        float solution = 0;

        if (inOrder)

        {

        	temp2 = values.pop();

            temp1 = values.pop();

        }

        else

        {

            temp1 = values.pop();

            temp2 = values.pop();

        }

        if (topOp.equals("/"))

            solution = temp1 / temp2;       

        else if (topOp.equals("*"))

        	solution = temp1 * temp2;

        else if (topOp.equals("+"))

            solution = temp1 + temp2;

        else if (topOp.equals("-"))  

            solution = temp1 - temp2;

        values.push(solution);*/
     
    
    private String stackToString(Stack<String> input)
    {		
		String value = input.pop();
		String output ="";
		
		if (value.equals(")"))
		{		
			while (!(value= input.pop()) .equals("("))
			{			
				output = value + output;		
			}
		} 
		else
		{
			while (!(value= input.pop()) .equals("["))
			{	
				output = value +output;
				
			}
		}
		
		return output;
	}
    private String paren(String input, int start) 
    {
		String toPush=input.charAt(start)+"";
		
		int index = start+1;
		Stack<String> st = new Stack<>();
		st.push(toPush);
		
		while (input.charAt(index)!= ')')
		{
			toPush ="";
			int track = index;
			while (Character.isLetter(input.charAt(index)) 
					|| Character.isDigit(input.charAt(index))
					|| input.charAt(index)=='.'){
				
				toPush += input.charAt(index);
				index++;
			}
			if (!(toPush.isEmpty()) ){
				
				st.push(toPush);
				toPush = "";
			}
			if (input.charAt(index)== '(' || input.charAt(index)== '['){
				
				if (input.charAt(index)== '(')
				{
					input = paren (input, index);
				}
				else
				{
					input = arren(input, st.pop(),index);
					index= track;
					
				}
			}
			else if (input.charAt(index)!= ')')
			{
				toPush = input.charAt(index)+"";
				st.push(toPush);
				index ++;
			}
		}  
		
		toPush =input.charAt(index)+"";
		st.push(toPush);	
		String sub = input.substring(start,index+1);		
		String output = calculation(stackToString(st));
		output = input.replace(sub, output);
		return output;	
	}
    
    public float evaluate()
    {	
    	String input = expr;   	
    	input= input.replace(" ","");	
    	int bracket = input.indexOf('(');   	
		int arrBrac =input.indexOf('[');
		
			while (bracket >= 0 || arrBrac >=0)
			{				
				if ((bracket< arrBrac && bracket>=0) ||(bracket>arrBrac && arrBrac<0) )
				{
					input = paren(input,bracket);					
					bracket = input.indexOf('(');					
					arrBrac =input.indexOf('[');				
				}
				else
				{					
					String name = "";					
					int track = arrBrac -1;
					
					while (track >=0 &&Character.isLetter(input.charAt(track)))
					{					
						name = input.charAt(track)+ name;						
						track--;
					}					
					input = arren(input,name, arrBrac);					
					bracket = input.indexOf('(');					
					arrBrac =input.indexOf('[');
				}
			}
			
			float value1 = Float.parseFloat(calculation(input));
		
    		return value1;
    }
    
    private String solution(String left,String oper, String right)
    {
    	float solve =0;	
    	if (oper.isEmpty()&& right.isEmpty()&& !(left.isEmpty()))
    	{
    		return	left = findScalValue(left);
    	}
    	StringTokenizer letter= new StringTokenizer(left,"-");
    	StringTokenizer letter2= new StringTokenizer(right,"-");
    	String let ="";
    	String ret ="";
    	while(letter.hasMoreElements()|| letter2.hasMoreTokens())
    	{
    		if (letter.hasMoreTokens())
    		{
    		let = letter.nextToken();
    		}
    		if(letter2.hasMoreElements())
    		{
    		ret = letter2.nextToken();
    		}
    	}
    	if (!(let.isEmpty())&&Character.isLetter(let.charAt(0))
    			|| !(ret.isEmpty())&&Character.isLetter(ret.charAt(0)))
    	{  	
    	if (Character.isLetter(let.charAt(0)))
    	{	
    		left= left.replace(let,findScalValue(let));   		
    	}
    	if (Character.isLetter(ret.charAt(0)))
    	{ 		
    		right= right.replace(ret,findScalValue(ret));
    	} 	
    }
    	switch (oper)
    	{
    		case "*":
    			solve = Float.parseFloat(left) * Float.parseFloat(right); 
    			break;
    		case "/" : 
    			solve = Float.parseFloat(left) / Float.parseFloat(right);
    			break;
    		case "-" :
    			if (left.equals("-"))
    			{
    				solve = Float.parseFloat(right); break;
    			}
    			solve = Float.parseFloat(left) -Float.parseFloat(right);
    			break;
    		case "+" :
    			solve = Float.parseFloat(left) + Float.parseFloat(right); 
    			break;
    	}
    	
     String output = String.valueOf(solve); 
     return output;   		
    }
 
    private String calculation(String input)
    {
    	int mul = input.indexOf("*");
    	int sub = input.indexOf("-"); 
    	int add = input.indexOf("+"); 
    	int div = input.indexOf("/");
    	String l="";
    	String r="";
    	String oper = "";
    	boolean subexceptional= (mul==-1 && div ==-1 && add==-1);
    	boolean sub2 = true;
    	while(!(add==-1 && mul ==-1 &&  div==-1 && sub==-1))
    	{
    		if((subexceptional&&  !(sub2) && sub<=0))
    		{	
			break;
    		}
		if(mul!=-1 || div!=-1)
		{
			/* if (!operators.isEmpty())

        {

			String topOp = operators.peek();

			if (topOp.equals("/") || topOp.equals("*"))

				processStack(operators, values, true);

        }*/
			while (!(mul ==-1 && div ==-1))
			{			
				if( (mul<div && mul!=-1) || (mul>div && div==-1))
				{	
				    int left = mul-1;		
				    int right = mul+1;
				while (( left>=0&&Character.isLetter(input.charAt(left)))
						|| (left>=0&&Character.isDigit(input.charAt(left)))
						|| left>=0 && input.charAt(left)=='.')
				{	
					l= input.charAt(left) + l;
					left--;	
				}
				oper+= input.charAt(mul);
				
				while ((right<= input.length()-1&& Character.isLetter(input.charAt(right)))
						|| (right<= input.length()-1&&Character.isDigit(input.charAt(right)))
						|| right<=input.length()-1&& (input.charAt(mul+1)== '-'
						||input.charAt(right)=='.'))
				{				
					r+= input.charAt(right);
					right++;					
				}
				String value = solution(l,oper,r);
				String regex = input.substring(left+1, right);
				input =input.replace(regex, value);
				mul =input.indexOf("*");
				div =input.indexOf("/");
				add =input.indexOf("+");
				sub =input.indexOf("-");
				l="";
				r="";
				oper= "";
				}
				else
				{
				int left = div-1;			
				int right = div+1;				
				while ((left >= 0 &&Character.isLetter(input.charAt(left)))
						|| (left >= 0 &&Character.isDigit(input.charAt(left)))
						|| (left>=0 && input.charAt(left)=='.'))
				{					
					l = input.charAt(left) + l;
					left--;					
				}
				oper += input.charAt(div);
				while (right<= input.length()-1 &&Character.isLetter(input.charAt(right))
						|| (right<= input.length()-1 &&Character.isDigit(input.charAt(right)))
						|| (right<=input.length()-1 && (input.charAt(div+1)== '-'
						||input.charAt(right)=='.')))
				{	
					r+= input.charAt(right);
					right++;					
				}
				String value = solution(l,oper,r);
				String regex = input.substring(left+1, right);
				input = input.replace(regex, value);
				div = input.indexOf("/");
				mul =input.indexOf("*");
				sub =input.indexOf("-");
				add =input.indexOf("+");
				l="";
				r="";
				oper= "";
				}
			}
	
		} 		
		else if (add != -1 || sub != -1)
		{			
			while (!(add==-1 && sub ==-1))
			{				
				if ((add<sub && add!=-1) || (add>sub && sub==-1))
				{
					 int left = add-1;		
					 int right = add+1;
					 while (left>=0 &&(Character.isLetter(input.charAt(left)))
							 || (left >=0 &&(Character.isDigit(input.charAt(left))))
							 || (left>=0 && input.charAt(left)=='.')){
							
							l = input.charAt(left) + l;
							left--;							
						}
						oper += input.charAt(add);
						
						while (right<= input.length()-1 &&Character.isLetter(input.charAt(right))
								|| (right<= input.length()-1 &&Character.isDigit(input.charAt(right)))
								|| (right<=input.length()-1 && ( input.charAt(add+1)== '-'
								||input.charAt(right)=='.')))
						{							
							r+= input.charAt(right);
							right++;
						}
						String value = solution(l,oper,r);
						String regex = input.substring(left+1, right);
						input =input.replace(regex, value);
						add= input.indexOf("+");
						mul =input.indexOf("*");
						sub =input.indexOf("-");
						div =input.indexOf("/");
						r= "";
						l="";
						oper="";
						
						}
						else 
						{					
							subexceptional= (mul==-1 && div ==-1 && add==-1);
							sub2 = checkNeg(input);
							  if (subexceptional && !(sub2) && sub <=0)
							  {
								 break;
							  }
						
							int left = sub-1;		
							int right = sub+1;
							while (left >= 0 && Character.isLetter(input.charAt(left))
									|| left>=0 && Character.isDigit(input.charAt(left))
									|| (left>=0 && input.charAt(left)=='.'))
							{
							l = input.charAt(left) +l;
							left--;
						}
						oper += input.charAt(sub);
						
						while ((right<=input.length()-1)&&Character.isLetter(input.charAt(right))
								||(right<=input.length()-1)&& Character.isDigit(input.charAt(right))
								|| (right<=input.length()-1 && input.charAt(right)=='.'))
						{							
							r+= input.charAt(right);
							right++;						
						}
						if (l.isEmpty())
						{
							
							l = oper+r;
							char temp = input.charAt(0);
							input= input.substring(1);
							r="";
							oper="";
							oper += input.charAt(right-1);
							r+= input.charAt(right);
							right++;
							while ((right<=input.length()-1)&&Character.isLetter(input.charAt(right))
									||(right<=input.length()-1)&& Character.isDigit(input.charAt(right))
									|| (right<=input.length()-1 && input.charAt(right)=='.'))
							{
								r+= input.charAt(right);
								right++;
							}
							input = temp + input;
							right++;
						}
						
						String value = solution(l,oper,r);
						String regex = input.substring(left+1, right);
						input =input.replace(regex, value);
						sub = input.indexOf('-');
						mul =input.indexOf("*");
						add =input.indexOf("+");
						sub =input.indexOf("-");
						l= "";
						r= "";
						oper="";					
						}
				}
			}	
    	}
    					if (Character.isLetter(input.charAt(0)))
    					{	
    					input = solution(input, "","");
    					}
    					return input;
    }	
    


	private  boolean checkNeg(String input)
	{
		int count=0;
		for (int i=0; i<input.length()-1;i++)
		{
	
			if (input.charAt(i)== '-')
			{
				count++;
			}
			if (count>1)
			{
				return true;	
			}
		}
		return false;
	}
	
	
	private String arren(String input, String name, int start)
	{		
		String toPush=input.charAt(start)+"";		
		int index = start+1;
		Stack<String> st = new Stack<>();
		st.push(toPush);	
		while (input.charAt(index)!= ']')
		{
			toPush ="";
			int track= index;			
			while (Character.isLetter(input.charAt(index))
					|| Character.isDigit(input.charAt(index))
					|| input.charAt(index)=='.'
					|| input.charAt(index)=='-')
			{
				
				toPush += input.charAt(index);
				index++;
			}
			if (!(toPush.isEmpty()) )
			{
				st.push(toPush);
				toPush = "";
			}
			if (input.charAt(index)== '(' || input.charAt(index)== '[')
			{			
				if (input.charAt(index)== '(')
				{	
					input = paren (input, index);			
				}
				else
				{
					input = arren(input, st.pop(),index);
					index=track;					
				}
			}
			else if (input.charAt(index)!= ']')
			{				
				toPush = input.charAt(index)+"";
				st.push(toPush);
				index ++;
			}
		}
		toPush = input.charAt(index)+"";	
		st.push(toPush);	
		String solution = stackToString(st);
		String sub = name+"["+solution+"]";;
		solution = calculation(solution);	
		String output = findArrValue( name, (int)Float.parseFloat(solution));	
		output = input.replace(sub, output);
		return output;
	}
	
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
