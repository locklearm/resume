#include <stdio.h>
#include <string.h>
#include <math.h>

/* Complete the function below to print 2 integers separated by a single space which will be your next move 
 */
void nextMove(char player, char ar[3][3])
{
	int move[2] = {5,5};
        
    //TESTING
	char errm[1000];
    sprintf(errm, "@@Player char:%c dig:%d", player, player);
    perror(errm);
	//END TESTING
	
	//Three in a row possible streaks
	char streaks[8][3] = {
							{ar[0][0], ar[0][1], ar[0][2]},  //Top row			0
							{ar[1][0], ar[1][1], ar[1][2]},  //Middle row		1
							{ar[2][0], ar[2][1], ar[2][2]},  //Bottom row		2
							{ar[0][0], ar[1][0], ar[2][0]},  //Left col			3
							{ar[0][1], ar[1][1], ar[2][1]},  //Middle Col		4
							{ar[0][2], ar[1][2], ar[2][2]},  //Right col		5
							{ar[2][2], ar[1][1], ar[0][0]},  //Backslash		6
							{ar[2][0], ar[1][1], ar[0][2]}   //Forward slash	7
						};
	// {streak1_idx, streak2_idx, intersect row, intersect col}
	int fork_streaks[22][4] = 	{	{3,0,0,0},
									{3,1,1,0},
									{3,2,2,0},
									{4,0,0,1},
									{4,1,1,1},
									{4,2,2,1},
									{5,0,0,2},
									{5,1,1,2},
									{5,2,2,2},
									{3,7,2,0},
									{3,6,0,0},
									{4,7,1,1},
									{4,6,1,1},
									{5,7,0,2},
									{5,6,2,2},
									{0,7,0,2},
									{0,6,0,0},
									{1,7,1,1},
									{1,6,1,1},
									{2,7,2,0},
									{2,6,2,2},
									{6,7,1,1}  //X
								};
	int streak_idx[8][3][2] = {
								{{0,0},{0,1},{0,2}},
								{{1,0},{1,1},{1,2}},
								{{2,0},{2,1},{2,2}},
								{{0,0},{1,0},{2,0}},
								{{0,1},{1,1},{2,1}},
								{{0,2},{1,2},{2,2}},
								{{2,2},{1,1},{0,0}},
								{{2,0},{1,1},{0,2}}
							};
	char other_player = (player == 'X') ? 'O' : 'X';
	int move_count = 0;
	for (int i = 0; i < 3; i++)
	{
		for (int j = 0; j < 3; j++)
		{
			if (ar[i][j] != '_')
			{
				move_count++;
			}
		}
	}
	
	
	//If first move, take corner
	if (move_count == 0)
	{
		move[0] = 0;
		move[1] = 0;
	}
	
	//Find winning move
	if (move[0] == 5)
	{
		for (int i = 0; i < 8; i++)
		{
			if (streaks[i][0] == player  && streaks[i][1] == player && streaks[i][2] == '_')
			{
				move[0] = streak_idx[i][2][0];
				move[1] = streak_idx[i][2][1];
			}
			else if (streaks[i][0] == player  && streaks[i][2] == player && streaks[i][1] == '_')
			{
				move[0] = streak_idx[i][1][0];
				move[1] = streak_idx[i][1][1];
			}
			else if (streaks[i][1] == player  && streaks[i][2] == player && streaks[i][0] == '_')
			{
				move[0] = streak_idx[i][0][0];
				move[1] = streak_idx[i][0][1];
			}
		}
	}
	
	//Find block move
	if (move[0] == 5)
	{
        //TESTING
        sprintf(errm, "Enter block move set, opp = %c", other_player);
        perror(errm);
        //END TESTING
		for (int i = 0; i < 8; i++)
		{
            //TESTING
            sprintf(errm, "Loop iter %d, opp = %c", i, other_player);
            perror(errm);
            //END TESTING
			if (streaks[i][0] == other_player  && streaks[i][1] == other_player)
			{
                //TESTING
                sprintf(errm, "@@Found opp combo 1, iter %d, opp = %c", i, other_player);
                perror(errm);
                //END TESTING
				if (ar[streak_idx[i][2][0]][streak_idx[i][2][1]] == '_')
				{
					move[0] = streak_idx[i][2][0];
					move[1] = streak_idx[i][2][1];
				}
			}
			else if (streaks[i][0] == other_player  && streaks[i][2] == other_player)
			{
                //TESTING
                sprintf(errm, "@@Found opp combo 2, iter %d, opp = %c", i, other_player);
                perror(errm);
                //END TESTING
				if (ar[streak_idx[i][1][0]][streak_idx[i][1][1]] == '_')
				{
					move[0] = streak_idx[i][1][0];
					move[1] = streak_idx[i][1][1];
				}
			}
			else if (streaks[i][1] == other_player  && streaks[i][2] == other_player)
			{
                //TESTING
                sprintf(errm, "@@Found opp combo 3, iter %d, opp = %c", i, other_player);
                perror(errm);
                //END TESTING
				if (ar[streak_idx[i][0][0]][streak_idx[i][0][1]] == '_')
				{
					move[0] = streak_idx[i][0][0];
					move[1] = streak_idx[i][0][1];
				}
			}
		}
	}
	
	//Find win fork
	if (move[0] == 5)
	{
		for (int i = 0; i < 22; i++)
		{
			
			
			int streak1_data[2] = {0, 0};  //{blanks, non-intersect player}
			int streak2_data[2] = {0, 0};  //{blanks, non-intersect player}
			for (int j = 0; j < 3; j++)
			{
				if (streaks[fork_streaks[i][0]][j] == '_')
				{
					streak1_data[0]++;
				}
				else if (streaks[fork_streaks[i][0]][j] == player
						&& !(
							streak_idx[fork_streaks[i][0]][j][0] == fork_streaks[i][2]
							&& streak_idx[fork_streaks[i][0]][j][1] == fork_streaks[i][3]
							)
						)
				{
					streak1_data[1]++;
				}
				if (streaks[fork_streaks[i][1]][j] == '_')
				{
					streak2_data[0]++;
				}
				else if (streaks[fork_streaks[i][1]][j] == player
						&& !(
							streak_idx[fork_streaks[i][1]][j][0] == fork_streaks[i][2]
							&& streak_idx[fork_streaks[i][1]][j][1] == fork_streaks[i][3]
							)
						)
				{
					streak2_data[1]++;
				}
			}
			if ( streak1_data[0] == 2 && streak2_data[0] == 2 && streak1_data[1] == 1 && streak2_data[1] == 1 )
			{
				move[0] = fork_streaks[i][2];
				move[1] = fork_streaks[i][3];
			}
		}
	}
	
	//Find fork block
	if (move[0] == 5)
	{
		
		int fork_count = 0;
		int found_forks[10] = {50,50,50,50,50,50,50,50,50,50};
		
		for (int i = 0; i < 22; i++)
		{
			
			
			int streak1_data[2] = {0, 0};  //{blanks, non-intersect other_player}
			int streak2_data[2] = {0, 0};  //{blanks, non-intersect other_player}
			for (int j = 0; j < 3; j++)
			{
				if (streaks[fork_streaks[i][0]][j] == '_')
				{
					streak1_data[0]++;
				}
				else if (streaks[fork_streaks[i][0]][j] == other_player
						&& !(
							streak_idx[fork_streaks[i][0]][j][0] == fork_streaks[i][2]
							&& streak_idx[fork_streaks[i][0]][j][1] == fork_streaks[i][3]
							)
						)
				{
					streak1_data[1]++;
				}
				if (streaks[fork_streaks[i][1]][j] == '_')
				{
					streak2_data[0]++;
				}
				else if (streaks[fork_streaks[i][1]][j] == other_player
						&& !(
							streak_idx[fork_streaks[i][1]][j][0] == fork_streaks[i][2]
							&& streak_idx[fork_streaks[i][1]][j][1] == fork_streaks[i][3]
							)
						)
				{
					streak2_data[1]++;
				}
			}
			if ( streak1_data[0] == 2 && streak2_data[0] == 2 && streak1_data[1] == 1 && streak2_data[1] == 1 )
			{
				
				found_forks[fork_count++] = i;
			}
		}
		
		//Deal with forcing out a fork block
		if (fork_count == 1)
		{
			move[0] = fork_streaks[found_forks[0]][2];
			move[1] = fork_streaks[found_forks[0]][3];
		}
		else if (fork_count > 1)
		{
			//Find attack moves
			//24* {{attack move},{forced defence location}}
			int attack_moves[24][2][2] = {{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}},{{5,5},{5,5}}};
			int cur_idx = 0;
			//First get the possible attack moves
			for (int i = 0; i < 8; i++)
			{
				if (streaks[i][0] == player && streaks[i][1] == '_' && streaks[i][2] == '_')
				{
					//First pair
					attack_moves[cur_idx][0][0] = streak_idx[i][1][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][1][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][2][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][2][1];
					//Second pair
					attack_moves[cur_idx][0][0] = streak_idx[i][2][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][2][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][1][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][1][1];
				}
				if (streaks[i][1] == player && streaks[i][0] == '_' && streaks[i][2] == '_')
				{
					//First pair
					attack_moves[cur_idx][0][0] = streak_idx[i][0][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][0][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][2][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][2][1];
					//Second pair
					attack_moves[cur_idx][0][0] = streak_idx[i][2][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][2][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][0][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][0][1];
				}
				if (streaks[i][2] == player && streaks[i][1] == '_' && streaks[i][0] == '_')
				{
					//First pair
					attack_moves[cur_idx][0][0] = streak_idx[i][1][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][1][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][0][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][0][1];
					//Second pair
					attack_moves[cur_idx][0][0] = streak_idx[i][0][0];
					attack_moves[cur_idx][0][1] = streak_idx[i][0][1];
					attack_moves[cur_idx][1][0] = streak_idx[i][1][0];
					attack_moves[cur_idx++][1][1] = streak_idx[i][1][1];
				}
			}
			//Now find the attack moves that don't force into an other_player fork
			for (int i = 0; i < 24; i++)
			{
				for (int j = 0; j < 10; j++)
				{
					if (found_forks[j] != 50)
					{
						if ( attack_moves[i][1][0] == fork_streaks[found_forks[j]][2]
							&& attack_moves[i][1][1] == fork_streaks[found_forks[j]][3])
						{
							attack_moves[i][0][0] = attack_moves[i][0][1] = 5;
						}
					}
				}
			}
			//Now pick one of our found attack moves
			for (int i = 0; i < 24; i++)
			{
				if (attack_moves[i][0][0] != 5)
				{
					move[0] = attack_moves[i][0][0];
					move[1] = attack_moves[i][0][1];
				}
			}
		}
	}
	
	//Make center move
	if (move[0] == 5)
	{
		if (ar[1][1] == '_')
		{
			move[0] = 1;
			move[1] = 1;
		}
	}
	
	//Make corner move
	if (move[0] == 5)
	{
		if (ar[0][0] == '_')
		{
			move[0] = 0;
			move[1] = 0;
		}
		else if (ar[2][2] == '_')
		{
			move[0] = 2;
			move[1] = 2;
		}
		else if (ar[0][2] == '_')
		{
			move[0] = 0;
			move[1] = 2;
		}
		else if (ar[2][0] == '_')
		{
			move[0] = 2;
			move[1] = 0;
		}
	}
	
	//Make side move
	if (move[0] == 5)
	{
		if (ar[1][0] == '_')
		{
			move[0] = 1;
			move[1] = 0;
		}
		else if (ar[0][1] == '_')
		{
			move[0] = 0;
			move[1] = 1;
		}
		else if (ar[2][1] == '_')
		{
			move[0] = 2;
			move[1] = 1;
		}
		else if (ar[1][2] == '_')
		{
			move[0] = 1;
			move[1] = 2;
		}
	}
	
	//Print move
	printf("%d %d", move[0], move[1]);


}


int main() {

    int i;
    char player;
    char board[4][3];

    //If player is X, I'm the first player.
    //If player is O, I'm the second player.
    scanf("%c", &player);

    //Read the board now. The board is a 3x3 array filled with X, O or _.
    for(i=0; i<3; i++) {
        scanf("%s[^\n]%*c", board[i]);
    }
  
	nextMove(player,board);
    return 0;
}