//Equipe: LG_FERA_2021
//Membros da equipe: Pedro Goldoni Magri
//                   Kauan Matos Lopes da Silva
//                   Nathan Enrico Romero

//Desenvolvido para ser usado na competição Robocode de 2021
//Etec Lauro Gomes


//Pacote onde o arquivo do robô está localizado
package prog.Robocode;

//Bibliotecas necessárias
import robocode.*;
import java.awt.*;
import java.awt.geom.*;
import robocode.util.Utils;

//Início da classe do robô
public class LG_FERA_2021 extends AdvancedRobot
{
    // Declaração de classes
    private guess_fator_targeting arma = new guess_fator_targeting();
    private movimentos mov = new movimentos();

    // Declaração de variáveis globais
    boolean movendo_enfrente; //variável relacionada ao movimento do robô, true quando está andando para frente

    //variáveis necessárias para a estratégia de adivinhar o futuro local do inimigo
    private static int gft_tamanho = 25 ;
    private static int gft_centro = (gft_tamanho - 1) / 2 ;
    private static int [] fatores_previsao = new int [gft_tamanho] ;
    private Point2D local_alvo ;

    //Evento acionado quando atngir o oponente
    public void onHitRobot(HitRobotEvent e)
    {
        //Ativação do método reverter_direção
        reverter_direcao();
    }

    //Evento acionado quando atingir uma parede
    public void onHitWall(HitWallEvent e)
    {
        //Ativação do método reverter_direção
        reverter_direcao();
    }

    //Método que inverte a direção de movimento do robô
    public void reverter_direcao()
    {
        //Faz com que se estiver se movendo para frente passe a se mover para trás e vice-versa
        if (movendo_enfrente)
        {
            setBack(40000);
            movendo_enfrente = false;
        } else
        {
            setAhead(40000);
            movendo_enfrente = true;
        }
    }

    //Evento acionado quando o radar detectar um oponente
    public void onScannedRobot(ScannedRobotEvent e)
    {
        //Ligação do evento com o mesmo em classes
        mov.onScannedRobot(e);
        arma.onScannedRobot(e) ;

        //Variáveis necessárias para o que o radar possa rastrear o alvo
        double angulo_do_inimigo = getHeadingRadians() + e.getBearingRadians();
        double volta_inimigo = Utils.normalRelativeAngle(angulo_do_inimigo - getRadarHeadingRadians());
        double volta_extra = Math.atan(36.0 / e.getDistance()) * (volta_inimigo);

        //Faz com que o radar siga o oponente
        setTurnRadarRightRadians (volta_inimigo + volta_extra) ;
    }

    public double pegar_angulo_absoluto (ScannedRobotEvent e)
    {
        //Cálculo que retorna um angulo mais preciso
        return e.getBearingRadians() + getHeadingRadians() ;
    }

    public double pegar_angulo_absoluto (Point2D fonte , Point2D alvo)
    {
        //Cálculo que retorna as coordenadas do inimigo
        return Math.atan2 (alvo.getX() - fonte.getX() ,alvo.getY() - fonte.getY()) ;
    }

    public double angulo_escape_maximo (double forca_tiro)
    {
        //Retorna o ângulo limite do radar para o inimigo atingir
        return Math.asin(8 / Rules.getBulletSpeed(forca_tiro)) ;
    }

    public int pegar_direcao (ScannedRobotEvent e)
    {
        //cálculos que mostram a direção e velocidade do inimigo
        double angulo = pegar_angulo_absoluto (e) ;
        double lateral = Math.sin(e.getHeadingRadians() - angulo) ;
        return lateral * e.getVelocity() < 0 ? -1 : 1 ;
    }

    public Point2D projeto(Point2D fonte , double angulo , double tamanho)
    {
        //calcula a projeção
        return new Point2D.Double (fonte.getX() + Math.sin(angulo) * tamanho,
                fonte.getY() + Math.cos(angulo) * tamanho);
    }

    //método que calcula a melhor força para o tiro com relação à nossa energia e na distância do inimigo
    public double decidir_forca_tiro ( ScannedRobotEvent e )
    {
        double forca_tiro = getOthers() == 1 ? 2.0 : 3.0 ;
        if (e.getDistance() < 300 && e.getDistance() > 200)
        {
            forca_tiro = 1.0 ;
        }
        else if(e.getDistance() <= 200)
        {
            forca_tiro = 3.0 ;
        }
        if (getEnergy() < 1)
        {
            forca_tiro = 0.1 ;
        }
        else if(getEnergy() < 10)
        {
            forca_tiro = 1.0 ;
        }
        return Math.min(e.getEnergy() / 4, forca_tiro) ;
    }

    /*classe do guess factor targeting (estratégia em que a arma não atira mirando no inimigo, e sim na direção em
     que ele pode estar em seguida)*/
    private class guess_fator_targeting
    {
        public void onScannedRobot(ScannedRobotEvent e)
        {
            //variáveis com cálculos necessários
            int direcao = pegar_direcao(e);
            double angulo = pegar_angulo_absoluto(e);
            double forca_tiro = decidir_forca_tiro(e);
            double fator = pegar_fator_do_indice(pegar_melhor_indice());
            double algulo_deslocado = direcao * fator * angulo_escape_maximo(forca_tiro);
            double volta_arma = Utils.normalRelativeAngle(angulo - getGunHeadingRadians() + algulo_deslocado);

            //move a arma para a direção de onde o inimigo pode estar no futuro
            setTurnGunRightRadians(volta_arma);
            local_alvo = projeto(new Point2D.Double(getX(), getY()), angulo, e.getDistance()) ;
            setTurnGunRightRadians (volta_arma) ;
            if (setFireBullet(forca_tiro) != null)
            {
                GFT_tiro gf_tiro = new GFT_tiro(e, forca_tiro) ;
                addCustomEvent (gf_tiro) ;
            }
        }

        //método que encontra o melhor indice do ângulo do inimigo
        private int pegar_melhor_indice()
        {
            int melhor_indice = gft_centro;
            for (int i = 0; i < gft_tamanho; i++)
            {
                if (fatores_previsao[i] > fatores_previsao[melhor_indice])
                {
                    melhor_indice = i;
                }
            }
            return melhor_indice;
        }

        //método que encontra os índices do ângulo do inimigo
        private double pegar_fator_do_indice(int indice)
        {
            return (double) (indice - gft_centro) /gft_centro;
        }
    }

    //classe complementar do guess factor targeting
    private class GFT_tiro extends Condition
    {
        //variáveis com dados necessários
        private Point2D local_arma ;
        private double valocidade_tiro ;
        private double angulo_absoluto ;
        private double direcao_lateral ;
        private double tempo ;

        //método que calcula o quão para frente a arma tem que estar para atingir o inimigo
        public GFT_tiro (ScannedRobotEvent e, double forca_tiro)
        {
            this.angulo_absoluto = pegar_angulo_absoluto(e) ;
            this.direcao_lateral = pegar_direcao(e) ;
            this.valocidade_tiro = Rules.getBulletSpeed(forca_tiro) ;
            this.local_arma = new Point2D.Double(getX(), getY()) ;
            this.tempo = getTime() ;
        }

        //método que testa se o tiro tem chance de atingir o inimigo
        public boolean test()
        {
            double distancia_andada = (getTime() - tempo) * valocidade_tiro ;
            double limite = local_arma.distance(local_alvo) - 18 ;
            if(distancia_andada > limite)
            {
                atualizar_fator();
                removeCustomEvent(this);
            }
            return false ;
        }

        //método que atualiza o local em que o inimigo pode estar
        private void atualizar_fator()
        {
            //variáveis que atualizam a previsão
            double angulo_atual = pegar_angulo_absoluto(local_arma, local_alvo) ;
            double angulo = Utils.normalRelativeAngle(angulo_atual - angulo_absoluto);
            double angulo_normalizado = angulo / direcao_lateral * gft_tamanho;
            int indice = (int) Math.round(angulo_normalizado + gft_centro) ;
            int indice_seguro = Math.max( 0, Math.min( indice, gft_tamanho - 1 ) ) ;
            fatores_previsao[indice_seguro]++;
        }
    }

    //classe reponsável pelos movimentos do corpo do robô
    private class movimentos
    {
        public void onScannedRobot(ScannedRobotEvent e)
        {
            /*quando o robô detecta o inimigo, ele o caça, e quando chega perto o suficiente circula ele,
            se estiver muito perto, ele recua.*/
            if(movendo_enfrente)
            {
                if (movendo_enfrente)
                {
                    setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 90));
                }
                else
                {
                    setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 90));
                }
            }
            if(e.getDistance() > 200)
            {
                setTurnRight(e.getBearing());

            }
            else if (e.getDistance() < 200)
            {
                setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() - 90));
            }
        }
    }

    public void run()
    {
        //configurações que melhoram os movimentos dos componentes do robô
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        //seleção de cores para cada parte do robô
        setBodyColor(Color.green);
        setGunColor(Color.blue);
        setRadarColor(Color.yellow);
        setScanColor(Color.white);
        setBulletColor(Color.white);

        //faz com que o robô ande para frente até que seja designada outra instrução
        setAhead(40000);
        movendo_enfrente = true;

        while (true)
        {
            /*faz com que o radar rode para a direita infinitamente (fazendo com que quando o radar perca
            o inimigo de vista, rode para encontrá-lo) */
            turnRadarRightRadians(Double.POSITIVE_INFINITY);
        }

    }

}
