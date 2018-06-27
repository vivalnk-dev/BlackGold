import time

def tohex(val, nbits):
  return hex((val + (1 << nbits)) % (1 << nbits))

print (tohex(-114, 8))[2:]


ts = int(time.time())
print ts
