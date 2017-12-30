# Transaction Challenge From N26 (by Roan Brasil Monteiro)

## Explanation

### Architecture

My first option was setup de package project with Onin Architecture, splitting basic the packages in API, Domain and Infractruture.
It is one architecture that my Squad team decided to use, one of the guys had been worked to GoEuro in Berlin and they use that way there.

Bellow all the references:
*Reference 1: https://dzone.com/articles/onion-architecture-is-interesting*
*Reference 2: http://jeffreypalermo.com/blog/the-onion-architecture-part-1/*
*Reference 3: http://jeffreypalermo.com/blog/the-onion-architecture-part-2/*
*Reference 4: http://jeffreypalermo.com/blog/the-onion-architecture-part-3/*
*Reference 5: http://jeffreypalermo.com/blog/the-onion-architecture-part-4/*
*Reference 6: http://tidyjava.com/onion-architecture-interesting/ *

### Running

To run the content, first at all you should

```
mvn clean install
```

After run this command that should take more than one minute (60 seconds) because of Unit Tests.
Please run the command bellow.

```
mvn spring-boot:run
```

### Consuming API

If you wanna consume the api, there are two services available.
The first one, is responsible to add transaction information (ammount and timestamp) about the transaction.
As you can see bellow, using the method post from Http request.

```
http://localhost:8080/transactions
```

The content in the body must be:

```
{
"amount": 12.3,
"timestamp": 1478192204000
}
```

Since the timestamp is not older than 60 seconds you are gonna take 201 (Created) Http Response, otherwhise you gonna take 204 (No Content) Http Response.

In the other hand, we have the statistics service, responsible for collecting statistics from the transaction in the last 60 seconds.
Using the method get from Http Request

```
http://localhost:8080/statistics
```

The response if there are transactions there, should be something like this.

```
{
"sum": 1000,
"avg": 100,
"max": 200,
"min": 50,
"count": 10
}
```

If there is no content, the EntityNotFoundException will be launched.

## This code was done by Roan Brasil Monteiro - roanbrasil@gmail.com