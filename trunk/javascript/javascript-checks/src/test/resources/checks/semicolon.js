function sayHello() {
  var a = { // NOK
    'i': 1,
    'j': 2
  }

  return 1 // NOK

  if (condition) { // OK
  }

  for (i = 0; i < 10; i++) { // OK
  }

  while (condition) { // OK
  }
}
