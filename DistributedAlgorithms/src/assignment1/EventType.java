package assignment1;

public enum EventType
{
	SEND
	{
		public String toString()
		{
			return "S";
		}
	},
	RECIEVE
	{
		public String toString()
		{
			return "R";
		}
	},
	INTERNAL
	{
		public String toString()
		{
			return "I";
		}
	}
}
