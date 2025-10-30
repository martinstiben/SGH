import React from 'react';
import { ScrollView, Image, View } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { styles } from '../styles/landingStyles';
import Header from '../components/Genericos/Header';
import InfoCard from '../components/Genericos/InfoCard';
import StatCard from '../components/Genericos/StatCard';
import { RootStackParamList } from '../navigation/types';
import { NativeStackNavigationProp } from '@react-navigation/native-stack';

type NavigationProp = NativeStackNavigationProp<RootStackParamList, 'Landing'>;

export default function LandingScreen() {
  const navigation = useNavigation<NavigationProp>();

  const handleLogin = () => {
    navigation.navigate('Login');
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Header
        title="Sistema de Gestión de Horarios"
        buttonLabel="Ingresar"
        onPress={handleLogin}
      />

      <Image source={require('../assets/images/logo.png')} style={styles.logo} />

      <InfoCard
        items={[
          '📅 Generación automática de horarios escolares',
          '👨‍🏫 Gestión completa de profesores y asignaturas',
          '📊 Reportes y estadísticas en tiempo real',
          '🔒 Seguridad y privacidad garantizada',
        ]}
      />

      <View style={styles.statsContainer}>
        <StatCard
          number="100%"
          label="Automatización en la creación de horarios"
          icon={require('../assets/images/trophy.png')}
        />
        <StatCard
          number="0"
          label="Conflictos de horarios garantizados"
          icon={require('../assets/images/rocket.png')}
        />
        <StatCard
          number="🔔"
          label="Notificaciones personalizables"
          icon={require('../assets/images/shapes.png')}
        />
      </View>
    </ScrollView>
  );
}
