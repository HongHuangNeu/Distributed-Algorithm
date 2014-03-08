package assignment2;

public enum State
{
	Request
	{
		public String toString()
		{
			return "R";
		}
	},
	Execute
	{
		public String toString()
		{
			return "E";
		}
	},
	Hold
	{
		public String toString()
		{
			return "H";
		}
	},
	Other
	{
		public String toString()
		{
			return "O";
		}
	}
	
}
