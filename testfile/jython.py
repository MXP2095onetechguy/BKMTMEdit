#!/usr/bin/env python3
import sys
import shlex

print("Hello World!")

argv = ""
for i in sys.argv:
	argv = argv + " " + i

print(str(shlex.split(argv)) + " is your args.")

sys.exit(0)