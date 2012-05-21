from random import choice

class MarkovChain:
	"""\
	The MarkovChain is the class that represents a simple mathematical markov chain.
	The class takes an input list (any ordered sequence of Objects) and analyzes it
	according to the given Markov level. The level defines how complex the analyze
	will be. The level is the amount of following elements that would affect the
	prediction of the next element in the list. For example, when having the string
	"banan" and the prediction is of the character following the final "n". If the
	Markov level is three the string "nan" will be considered when predicting the
	next element. If the level would have been 1, only the "n" would be used for the
	prediction, but it is easily understood that it's easier to predict the following
	character for "nan" than for "n", because "nan" has less possible followers than "n".
	
	The class can be used for predicting words or characters, or for predicting numbers
	in a squence of numbers. Bare in mind that the larger the input the better prediction,
	and also that the choosing of the Markov level is vital to the result. If the level
	is high, the processing will be slow, but the prediction will be better. But if the
	level is too high related to the input size, the prediction wouldn't be useful.
	A low level makes faster processing but not as good prediction capabilites."""
	
	def __init__(self, input, degree=1):
		if not input or not isinstance(input, basestring):
			raise ValueError("Input is not a string or is None")
		self.input = input
		self.degree = degree
		self.loop_around = True
		self.table = {}
		self.process()

	def process(self):
		self.table[''] = self.input[0]
		self.table[self.input[-self.degree:]] = ['']
		for i in xrange(len(self.input)-self.degree):
			slice = self.input[i:i+self.degree]
			val = self.input[i+self.degree]
			print "'%s'->'%s'" % (slice,val)
			self.table.setdefault(slice,[]).append(val)
		return self.table
	
	def next(self, seed):
		return choice(self.table[seed])
	
	def nextSequence(self, seed=None):
		if not seed:
			seed = choice(self.table.keys())
		s,c = seed, 'x'
		while c:
			c = self.next(s[-self.degree:]) #Pick last 'degree' number of items
			s += c
			print s
		return s

		
if __name__ == "__main__":
	sys.exit(main())