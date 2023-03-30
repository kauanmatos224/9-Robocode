package prog.Robocode;

// BLOCO DE IMPORTAÇÕES DE BIBLIOTECAS-----------------------
import robocode.*;
import java.awt.Color;
import java.awt.geom.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;
// BLOCO DE IMPORTAÇÕES DE BIBLIOTECAS-----------------------



// INÍCIO DO PROGRAMA--------------------------------------------------------------------------------------------------------------------------------------------
public class minRobot extends Robot
{


    public void run()
    {
	



        // BLOCO DE CORES DO ROBÔ------------------------------
		
		
        setBodyColor(Color.yellow); // COR DO CORPO DO ROBÔ
		
        setGunColor(Color.black); // COR DA ARMA DO ROBÔ
		
        setRadarColor(Color.red); // COR DO RADAR DO ROBÔ
		
        setScanColor(Color.white); // COR DA VARREDURA DO ROBÔ
		
        setBulletColor(Color.red); // COR DO TIRO DO ROBÔ
		

        // BLOCO DE CORES DO ROBÔ------------------------------
		





       //BLOCO DE DECLARAÇÃO DE VARIÁVEIS----
	   

        Object alvo = null;
        int cont = 0;
		

       //BLOCO DE DECLARAÇÃO DE VARIÁVEIS----








        // BLOCO DO LOOP PRINCIPAL DO ROBÔ------------------------------
        while(true)
        {
            scan();
            turnGunRight(10);
            ahead(50);
            turnLeft(25);
            cont ++;

            if(cont > 3)
            {
                turnGunLeft(30);
            }


            /* if(alvo == null)
            {
                turnGunRight(360);
            } */
			
        }
        // BLOCO DO LOOP PRINCIPAL DO ROBÔ------------------------------
    }






    // BLOCO DO EVENTO DE QUANDO ENCONTRAR UM ROBÔ PELO SCANNER------------------------------------------------------------------------
    public void onScannedRobot(ScannedRobotEvent e)
    {
	




        // Bloco de variáveis-------------
		
        double dist = e.getDistance();
		
        String nome = e.getName();
		
        // Bloco de variáveis---------------







         // --------------------------------------BLOCO DE CONDIÇÕES-----------------------------------------------------------------------------------------------------------------------------------------------------------------
		

		//TESTE LOGICO - 1-------------------------------------
        if(dist <= 135)
        {
		

			//TESTE LOGICO - 1.1------
            if(getGunHeat() == 0)
            {
                fire(3);
            }
            else
            {
                fire(1);
            }
			//TESTE LOGICO - 1.1-------
			

        }
        else
        {
		
			//TESTE LOGICO - 1.2----------
            if(getGunHeat() == 0)
            {
                fire(1);
            }
			//TESTE LOGICO - 1.2----------
			
        }
		
		//TESTE LOGICO - 1-------------------------------------
		

		//TESTE LOGICO - 1.2 (COMENTARIO KAUAN MATOS: !*DESCREVER O CÓDIGO, ESSE FALTA DESCRIÇÃO, SEMPRE COLOCAR DESCRIÇÃO NO CÓDIGO*!)-------------------
        if(nome != null)
        {
            System.out.println("Nome: " + nome + " Distância: " + dist);
        }
        else
        {
            System.out.println("");
        }
        //TESTE LOGICO - 1.2 (COMENTARIO KAUAN MATOS: !*DESCREVER O CÓDIGO, ESSE FALTA DESCRIÇÃO, SEMPRE COLOCAR DESCRIÇÃO NO CÓDIGO*!)-------------------
		
		
		// -----------------------------------BLOCO DE CONDIÇÕES-----------------------------------------------------------------------------------------------------------------------------------------------------------------


    }
    // BLOCO DO EVENTO DE QUANDO ENCONTRAR UM ROBÔ PELO SCANNER------------------------------------------------------------------------









    // BLOCO DO EVENTO DE QUANDO O ROBÔ FOR ATINGIDO POR UM DISPARO---------------------------------------------
    public void onHitByBullet(HitByBulletEvent e)
    {
		

	
		// VARIÁVEL PARA PEGAR A ANGULAÇÃO DA ARMA DE ACORDO COM A POSIÇÃO DO ADVERSÁRIO
        double angArma = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		




         // TESTE LOGICO - CONDIÇÕES DE ACORDO DE QUE ÂNGULO VEIO O DISPARO--------
        if(e.getBearing() % 180 == 0)
        {
            turnRight(e.getBearing() + 90);
            ahead(50);
            turnGunRight(angArma);
            fire(1);
        }
        else
        {
            turnRight(e.getBearing() - 90);
            ahead(50);
            turnGunRight(angArma);
            fire(1);
        }
        // TESTE LOGICO - CONDIÇÕES DE ACORDO DE QUE ÂNGULO VEIO O DISPARO--------
		


        scan();
		
    }
    // BLOCO DO EVENTO DE QUANDO O ROBÔ FOR ATINGIDO POR UM DISPARO---------------------------------------------






    // BLOCO DO EVENTO DE QUANDO O ROBÔ COLIDIR COM UMA PAREDE-----------
    public void onHitWall(HitWallEvent e)
    {
        fire(1);
        turnRight(e.getBearing() - 180);
        ahead(50);
        scan();
    }
    // BLOCO DO EVENTO DE QUANDO O ROBÔ COLIDIR COM UMA PAREDE-----------
	





    // BLOCO DO EVENTO PARA QUANDO O ROBÔ SE COLIDIR COM O ADVERSÁRIO---------------------------------------------
    public void onHitRobot(HitRobotEvent e)
    {
	

        // VARIÁVEL PARA PEGAR A ANGULAÇÃO DA ARMA DE ACORDO COM A POSIÇÃO DO ADVERSÁRIO
        double angArma = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		



        // VIRAR A ARMA NA DIREÇÃO DO ADVERSÁRIO
        turnGunRight(angArma);
		




        // TESTE LOGICO - COMO DEVE ATIRAR DE ACORDO COM A TEMPERATURA DA ARMA------
        if(getGunHeat() == 0)
        {
            fire(3);
        }
        else
        {
            fire(1);
        }
		// TESTE LOGICO - COMO DEVE ATIRAR DE ACORDO COM A TEMPERATURA DA ARMA------
		


    }
    // BLOCO DO EVENTO PARA QUANDO O ROBÔ SE COLIDIR COM O ADVERSÁRIO---------------------------------------------
	

}


// FIM DO PROGRAMA--------------------------------------------------------------------------------------------------------------------------------------------
