function sayHello() {

  if (condition) doSomething(); // NOK

  for (i = 0; i < 10; i++) doSomething(); // NOK

  while (condition) doSomething(); // NOK

  do something() while (condition); // NOK

  if (condition) { // OK
  }

  for (i = 0; i < 10; i++) { // OK
  }

  while (condition) { // OK
  }

  do { // OK
    something();
  } while (condition);

}
