This app uses Docker with Postgre DB databse for Customers.

Endpoints:
 -	http://localhost:8080/api/customers/{accountNumber}?pin={pin} (Balance Check and Maximum withdrawal)
 -	http://localhost:8080/api/customers/{accountNumber}?pin={pin}&amount={amount} (Whitdrawal request)
