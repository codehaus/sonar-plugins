void function1(void);

void function2(void)
{
  void function3(void); /* NOK */
  
  {
    void function4(void); /* NOK */
  }
}

int main(int argc, char* argv[])
{
  return 0;
}
