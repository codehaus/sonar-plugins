struct mystruct {
  int member;
};

void misra_bulletin_board(void)
{
  int array[2];
  int i = 0;
  int *p = array;
  int a = 0, b = 0;
  int buffer[2];
  int index = 1;
  int pop_value;
  int push_value = 12;
  int *p1 = &a, *p2 = &b;
  int sum;
  
  struct mystruct my_struct;
  struct mystruct *my_struct_ptr = &my_struct;
  
  *p++; /* NOK, dereferences the result of "p++" which is "p" */
  
  (*p)++; /* 1 */
  array[i]++; /* 2 */
  my_struct.member++; /* 3 */
  my_struct_ptr->member++; /* 4 */
  
  a = b++; /* 5 */ /* NOK */
  pop_value = buffer[--index]; /* 6 */ /* NOK */
  buffer[index++] = push_value; /* 7 */ /* NOK */
  *p1++ = *p2++; /* 8 */ /* NOK */
  
  i = 3;
  do { /* nothing */ } while ((i--) > 0); /* 9 */ /* NOK */
  
  i = 0;
  sum += buffer[i++]; /* 10 */ /* NOK */
}

int main(int argc, char* argv[])
{
  int array[1];
  int a = 0;
  int b = 0;
  int c = 0;
  
  array[0]++;
  array[a]++;
  array[a++]++; /* NOK */
  
  c = ++a + b--; /* NOK */
  
  c = ++a + 1; /* NOK */
  c = 1 + ++a; /* NOK */
  c = (++a - 10) && 123; /* NOK */
  
  c = --a + 1; /* NOK */
  c = 1 + --a; /* NOK */
  c = (--a - 10) && 123; /* NOK */
  
  c = a++ + 1; /* NOK */
  c = 1 + a++; /* NOK */
  c = (a++ - 10) && 123; /* NOK */
  
  c = a-- + 1; /* NOK */
  c = 1 + a--; /* NOK */
  c = (a-- - 10) && 123; /* NOK */
  
  1 + a++; /* NOK */
  1 + ++a; /* NOK */
  1 + a--; /* NOK */
  1 + --a; /* NOK */
  (a-- - 10) && 123; /* NOK */
  (++a - 10) && 123; /* NOK */
  
  c = -a + 1;
  c = -a + -b;
  
  ++a;
  b = a + b;
  b--;
  
  a = 0;
  array[a++]; /* NOK */
  array[--a]; /* NOK */
  
  a = b = c = 0;
  
  misra_bulletin_board();
  
  return 0;
}
