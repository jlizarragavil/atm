This app uses Docker with Postgre DB databse for Customers.

Endpoints:
 -	http://localhost:8080/api/customers/{accountNumber}?pin={pin} (Balance Check and Maximum withdrawal. It also Shows the notes left on the ATM, I did it because it 	was easier for me to test the application)
 -	http://localhost:8080/api/customers/{accountNumber}?pin={pin}&amount={amount} (Whitdrawal request)
