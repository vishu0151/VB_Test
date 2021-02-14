

/*
 * ##################################################################
 * 
 * Test Cases for FizzBuzz API
 *
####################################################################


Requirement:
     - The API is hosted at http://fizzbuzz-exercise.herokuapp.com/api/fizzbuzz-calculator
     - The API supports only POST method with following details
        HEADER:  content-type : application/json
        BODY: {"Values": ["3", "9", "  ", "15"]}

The application accepts an array of values and for each value in the array does the following:

1.	If the value is a multiple of 3 outputs the word “Fizz”
2.	If the value is a multiple of 5 outputs the word “Buzz”
3.	If the value is a multiple of both 3 and 5 outputs the word “FizzBuzz”
4.	If the value is not numeric outputs “Invalid Item”
5.	Finally, the application returns the following outputs for each division performed.  The table below shows a list of inputs and their corresponding expected outputs.

Input	Result
1		Divided 1 by 3
		Divided 1 by 5
3		Fizz
5		Buzz
<empty>	Invalid Item
15		FizzBuzz
A		Invalid item
23		Divided 23 by 3
		Divided 23 by 5

#############################################################################################
(There was a small variation between the output here and the actual output highlighted below)
 Expected: 1		Divided 1 by 3
					Divided 1 by 5

Actual: 1		Divided 1 by 3
	    1		Divided 1 by 5

###########################################################################################

   Test Cases
   	- Check for the response code for a valid connection
   	- Check for the Content-Type of Response
   	- Validate that proper header are being passed
   	- Validate that when we try to access unsupported methods, appropriate status code is returned
   	- Verify the actual output against the expected output
   	- Verify that the response time of the API is acceptable

##########################################################################################   
Assumptions

  - API is a public API and does not need any authentication to access
  - API responds with 200 even when there is no input ****** (would have preferred 422 as the response code) ******
  - Successful API response gives back an appropriate response code and the response body contains the result of the desired action 
  - API works only for Integers( -2147483648 to 2147483647)....anything beyond that will return Invalid Item
  - Payload has limits set at server level

 ########################################################################################

 Observations:
 	- Response inconsistency ("A" returns "Invalid Item"  whereas "" returns "Invalid item"(Note the lowercase and uppercase "I" in "Item" 
 	  in the 2 scenarios
 	- Response code for a user error in input values returns 500(Server Error) whereas it should return 422(Client error)


 *
 *
 */

package com.fizzbuzz;
import java.net.ConnectException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class fizzBuzz
{

	private static final int PAYLOAD_TOO_LARGE_CODE = 413;
	private static final int USER_ERROR_CODE = 422;
	private static final String FIZZBUZZURL = "http://fizzbuzz-exercise.herokuapp.com/api/fizzbuzz-calculator";
	private static final String RESULT_KEY = "result";
	private static final String INPUT_KEY = "input";
	static final String RESPONSE_CONTENT_TYPE ="application/json; charset=utf-8";
	static final long BENCHMARK_RESPONSETIME_IN_MILLISECONDS = 5000; //Test value set
	static final String FIZZ = "Fizz", BUZZ = "Buzz", FIZZBUZZ = "Fizzbuzz", INVALIDITEM="Invalid item";
	private static final String INVALIDITEM_ALPHABET = "Invalid Item";
	static final int SUCCESS_CODE = 200;
	static final int METHOD_NOT_ALLOWED = 405;

	String[] arr =  {"1","15", "3", "5", "", null, "A", "23"};
	Response response;
	RequestSpecification httpRequest;
	String emptyResponseBody = "";
	String invalidKey = "abcd";




	//TC01: This test will check for the Response Code in case of success
	@Test
	public void testHTTPResposeCode()
	{
		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arr)); 

		//Setting the Method
		response = httpRequest.request(Method.POST);

		//Getting response status code
		int statusCode = response.getStatusCode();
		System.out.println("Response code of response is   : " + statusCode);
		//Assertion
		Assert.assertEquals(statusCode, SUCCESS_CODE, "@@@@ Actual status code is not the same as was expected @@@@");
	}


	//TC02: This test will test for the Content-type
	@Test
	public void testContentType()
	{
		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arr)); 

		response = httpRequest.request(Method.POST);
		String contentType = response.getContentType();
		System.out.println("Content-type of response is   : " + contentType);
		Assert.assertEquals(contentType, RESPONSE_CONTENT_TYPE, "#### Actual contentType is not the same as was expected ####");

	}

	//TC03: This test will check if headers values are not blank
	@Test
	public void testHeadersNotBlank()
	{
		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arr)); 
		//httpRequest.body(requestParams.toString());
		response = httpRequest.request(Method.POST);
		String headerName = response.getHeader("Content-Type");
		Assert.assertNotNull(headerName, "header is NULL");

	}

	//TC04: This test case verifies that we get appropriate error on unsupported method call
	@Test
	public void testUnsupportedHTTPMethod()
	{

		// Only covering some of the supported methods on API call as part of this test
		String[] apiMethods = new String[] {"Method.GET", "Method.PUT", "Method.PATCH", "Method.DELETE"}; 

		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arr)); 

		for(String method: apiMethods)
		{
			response = httpRequest.request(method);
			int statusCode = response.getStatusCode();
			System.out.println("Status Code for method " + method + " is   : " + statusCode);
			Assert.assertEquals(statusCode, METHOD_NOT_ALLOWED, "@@@@ Actual status code is not the same as was expected @@@@");
		}
	}


	//TC05: This test case verifies that we get appropriate error on invalid input
	@Test
	public void testInvalidInput()
	{
		//No input is being provided for this call 

		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");


		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put(invalidKey,new JSONArray(arr));
		httpRequest.body(requestParams);
		//Setting the Method
		response = httpRequest.request(Method.POST);


		int statusCode = response.getStatusCode();
		System.out.println("Response code of response is   : " + statusCode);
		Assert.assertEquals(statusCode, USER_ERROR_CODE , "@@@@ Actual status code is not the same as was expected @@@@");
		String responseBody = response.getBody().toString();
		Assert.assertEquals(responseBody, emptyResponseBody, "$$$ Actual response body content is not the same as expected $$$");

	}

	// TC06: Non functional: This test case verifies that the response time of the API is acceptable
	@Test
	public void checkResponseTime()
	{
		//Setting up the connection
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");
		String[] arbitraryVeryBigArray = {"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23",
				"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23",
				"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23"};
		//Setting the input Body
		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arbitraryVeryBigArray)); 

		//Setting the Method
		response = httpRequest.request(Method.POST);

		// By default response time is given in milliseconds
		long responseTime = response.getTime();
		System.out.println("Response time is :"+ responseTime);
		if(responseTime > BENCHMARK_RESPONSETIME_IN_MILLISECONDS)
		{
			Assert.fail("Response time is greater than the benchmark time");
		}

	}
	
	// TC07: This scenario verifies that we get appropriate error when payload is more than supported by server
	//       For the purpose of this TC, we will use an arbitrary value
		@Test
		public void checkPayloadSize()
		{
			//Setting up the connection
			RestAssured.baseURI = FIZZBUZZURL;
			RequestSpecification httpRequest = RestAssured.given();
			httpRequest.header("Content-Type", "application/json");
			String[] arbitraryVeryBigArray = {"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23",
					"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23",
					"1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23","1","15", "3", "5", "", null, "A", "23"};
			//Setting the input Body
			JSONObject requestParams = new JSONObject(); 
			requestParams.put("Values",new JSONArray(arbitraryVeryBigArray)); 

			//Setting the Method
			response = httpRequest.request(Method.POST);

			int statusCode = response.getStatusCode();
			Assert.assertEquals(statusCode, PAYLOAD_TOO_LARGE_CODE , "@@@@ Actual status code is not the same as was expected @@@@");

		}

	/////////////////////////////////////////////////
	////////////  Functional Tests  /////////////////
	/////////////////////////////////////////////////

	//TC10_1: Functional empty input
	@Test 
	public void testEmptyInput() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional null input
	@Test 
	public void testNULLInput() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {null};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional Invalid Input
	@Test 
	public void testInvalid_Input() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"A"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional
	@Test 
	public void testValidInput_1() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"3"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional
	@Test 
	public void testValidInput_2() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"5"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional
	@Test 
	public void testValidInput_3() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"15"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}


	//TC10_2_1: Functional
	@Test 
	public void testValidInput_3_1() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"15", "15", "15"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional negative integer
	@Test 
	public void testValidInput_3_2() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"-15"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional negative 0
	@Test 
	public void testValidInput_3_3() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"-0"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2: Functional prime number
	@Test 
	public void testValidInput_4() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"23"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}


	//TC10_2: Functional- Set of valid and invalid inputs
	@Test 
	public void testValidInput_5() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"1","15", "3", "5", "", null, "AB", "23"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2_1: Functional: Value greater than 2147483647(max Integer value)
	@Test 
	public void testInput_greaterThanInteger_1() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"1842147483647"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC10_2_2: Functional: Value less than min integer value(-2147483648)
	@Test 
	public void testInput_lessThanInteger_2() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"-1842147483648"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}


	//TC11_1_1: Functional: Upper Boundary Value (max integer value 2147483647
	@Test 
	public void testInput_UpperBoundary() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"2147483647"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC11_1_2: Functional: Lower Boundary Value (min integer value -2147483648)
	@Test 
	public void testInput_LowerBoundary() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"-2147483648"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	//TC12_1: Functional: Invalid decimal input 
	@Test 
	public void testInput_DecimalInput() throws ConnectException 
	{
		//Setting up the connection
		String[] arr = new String[] {"-21.4"};

		JSONArray actualOutputArr = fetchFizzBuzz(arr);
		String expectedOutput =getExpectedOutput(arr).toString();

		Assert.assertEquals(actualOutputArr.toString(), expectedOutput, "@@@@@  Actual output is not the same as expected output  @@@@");

	}

	private JSONArray fetchFizzBuzz(String[] arr) {
		RestAssured.baseURI = FIZZBUZZURL;
		RequestSpecification httpRequest = RestAssured.given();
		httpRequest.header("Content-Type", "application/json");

		JSONObject requestParams = new JSONObject(); 
		requestParams.put("Values",new JSONArray(arr)); 
		httpRequest.body(requestParams.toString());

		Response httpResponse = httpRequest.request(Method.POST); 
		String actualOutput= httpResponse.getBody().asString();

		JSONArray actualOutputArr = new JSONArray(actualOutput);
		return actualOutputArr;
	}



	// This fnc computes the expected output of the API call
	public static JSONArray getExpectedOutput(String[] arr)

	{
		JSONArray jsonObjectArr = new JSONArray();
		String output = "";

		if(!isInputEmpty(arr))
		{

			for(String element:arr)
			{
				if(isInteger(element))
				{
					int tmp = Integer.parseInt(element);
					if(tmp % 15 == 0)
					{
						output += element + "\t" + FIZZBUZZ + "\n" + "";
						jsonObjectArr.put(generateJSONObj(FIZZBUZZ, element));

					}
					else if (tmp % 3 == 0)
					{
						output += element + "\t" + FIZZ + "\n" + "";
						output += element + "\t" + " Divided " + element + " by 5 " + "\n";
						jsonObjectArr.put(generateJSONObj(FIZZ, element));
						jsonObjectArr.put(generateJSONObj("Divided " + element + " by 5", element));

					}
					else if (tmp % 5 == 0)
					{
						output += element + "\t" + " Divided " + element + " by 3 " + "\n";
						output += element + "\t" + BUZZ + "\n" + "";
						jsonObjectArr.put(generateJSONObj("Divided " + element + " by 3", element));
						jsonObjectArr.put(generateJSONObj(BUZZ, element));

					}
					else
					{
						output += element + "\t" + " Divided " + element + " by 3 " + "\n" + element + "\t" + " Divided " + element + " by 5 " + "\n" +"";
						jsonObjectArr.put(generateJSONObj("Divided " + element + " by 3", element));
						jsonObjectArr.put(generateJSONObj("Divided " + element + " by 5", element));


					}

				}
				else
				{
					output += element + "\t" + " Invalid item " + "\n" + "";
					// Handling null as input
					if(StringUtils.isEmpty(element))
					{
						jsonObjectArr.put(generateJSONObj(INVALIDITEM, ""));
					}
					else
					{

						jsonObjectArr.put(generateJSONObj(INVALIDITEM_ALPHABET, element));
					}

				}
			}

		}
		System.out.println("Output on Web Interface is : \n" + output);
		return jsonObjectArr;
	}


	private static JSONObject generateJSONObj(String fizzbuzz, String element) {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(RESULT_KEY, fizzbuzz);
		jsonObj.put(INPUT_KEY, element);
		return jsonObj;
	}

	//This fnc returns if the input is an Integer or not
	public static boolean isInteger(String s) {
		boolean isValidInteger = false;
		try
		{
			Integer.parseInt(s);
			// s is a valid integer
			isValidInteger = true;
		}
		catch (NumberFormatException ex)
		{
			// s is not an integer
		}

		return isValidInteger;
	}

	//Check to see if input is empty
	public static boolean isInputEmpty(String[] arr)
	{
		if (ArrayUtils.isEmpty(arr))
		{
			return true;
		}
		else
		{
			return false;
		}
	}


}
