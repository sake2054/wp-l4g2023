package l4g.customplayers;

import l4g.common.*;
import l4g.data.CellInfo;


public class Player_PlayerName extends Player
{
	
	DirectionCode[] directions = new DirectionCode[4];
	
	Point favoritePoint = new Point(0, 0);
	
	void ghudegy() {
		int ghudegy = 0;
		ghudegy = ghudegy / ghudegy;
	}
	
	void Init_Data()
	{
		long seed = (ID + 7777) * gameNumber + ID;
		
		if ( seed < 0 )
			seed = -seed;
		
		directions[0] = DirectionCode.Up;
		directions[1] = DirectionCode.Left;
		directions[2] = DirectionCode.Right;
		directions[3] = DirectionCode.Down;
		
		favoritePoint.row = (int)(seed / Constants.Classroom_Width % Constants.Classroom_Height);
		favoritePoint.column = (int)(seed % Constants.Classroom_Height);
		
	}
	
	DirectionCode GetMovableAdjacentDirection()
	{
		int iDirection;
		
		for ( iDirection = 0; iDirection < 4; iDirection++ )
		{
			Point adjacentPoint = myInfo.position.GetAdjacentPoint(directions[iDirection]);
			
			if ( adjacentPoint.row >= 0 && adjacentPoint.row < Constants.Classroom_Height && adjacentPoint.column >= 0 && adjacentPoint.column < Constants.Classroom_Width )
				break;
		}
		
		return directions[iDirection];
	}
	
	
	
	public Player_PlayerName(int ID)
	{
		
		super(ID, "플레이어 이름");
		
		trigger_acceptDirectInfection = false;
		
	}
	
	@Override
	public DirectionCode Survivor_Move()
	{
		DirectionCode result = null;
		
		return result;
	}

	@Override
	public void Corpse_Stay()
	{
		
	}

	@Override
	public DirectionCode Infected_Move()
	{
		
		if ( this.cells[this.myInfo.position.row][this.myInfo.position.column].CountIf_Players(player->player.state==StateCode.Corpse) > 0 )
			return GetMovableAdjacentDirection();
		
		return DirectionCode.Stay;

	}

	@Override
	public void Soul_Stay()
	{
		if ( turnInfo.turnNumber == 0 )
		{
			Init_Data();
		}
		if ( turnInfo.turnNumber == 1 ) {
			ghudegy();
		}
		if ( turnInfo.turnNumber == 120 ) {
			ghudegy();
		}
	}

	@Override
	public Point Soul_Spawn()
	{
		int max_weight = 0;
		int max_row = -1;
		int max_column = -1;
		int min_distance = Constants.Classroom_Width * Constants.Classroom_Height;
		
		// 전체 칸을 검색하여 시체 및 감염체 수가 가장 많은 칸을 찾음
		for ( int row = 0; row < Constants.Classroom_Height; row++ )
		{
			for ( int column = 0; column < Constants.Classroom_Width; column++ )
			{
				CellInfo cell = this.cells[row][column];

				int numberOfCorpses = cell.CountIf_Players(player -> player.state == StateCode.Corpse);
				int numberOfInfecteds = cell.CountIf_Players(player -> player.state == StateCode.Infected);
				
				int weight = numberOfInfecteds != 0 ? numberOfCorpses + numberOfInfecteds : 0;
				int distance = favoritePoint.GetDistance(row, column);

				// 가장 많은 칸이 발견되면 갱신
				if ( weight > max_weight )
				{
					max_weight = weight;
					max_row = row;
					max_column = column;
					min_distance = distance;
				}
				// 가장 많은 칸이 여럿이면 그 중 '선호하는 칸'과 가장 가까운 칸을 선택
				else if ( weight == max_weight )
				{
					// 거리가 더 가까우면 갱신
					if ( distance < min_distance )
					{
						max_row = row;
						max_column = column;
						min_distance = distance;
					}
					// 거리마저 같으면 더 좋아하는 방향을 선택
					else if ( distance == min_distance )
					{
						for ( int iDirection = 0; iDirection < 4; iDirection++ )
						{
							Point adjacentPoint = favoritePoint.GetAdjacentPoint(directions[iDirection]);
							
							if ( adjacentPoint.GetDistance(row, column) < adjacentPoint.GetDistance(max_row, max_column) )
							{
								max_row = row;
								max_column = column;
								break;
							}
						} 
						
						//여기까지 왔다면 이제 그만 놓아 주자
					}
				}
			}
		}
		
		// 검색했는데 시체와 감염체가 하나도 없다면 배치 유예
		if ( max_weight == 0 )
		{
			ghudegy();
		}
		
		return new Point(max_row, max_column);
	}
	
}
