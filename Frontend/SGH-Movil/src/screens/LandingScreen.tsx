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
        title="SGH"
        buttonLabel="Ingresar"
        onPress={handleLogin}
      />

      <Image source={require('../assets/images/logo.png')} style={styles.logo} />

      <InfoCard
        items={[
          '🌈 Proyecto Cero Harvard y psicología positiva',
          '🌸 45 años floreciendo en familia',
          '📍 Cll 24 # 3-52, Los Samanes',
          '📞 3158769862',
        ]}
      />

      <View style={styles.statsContainer}>
        <StatCard
          number="80+"
          label="Niños creciendo felices"
          icon={require('../assets/images/trophy.png')}
        />
        <StatCard
          number="20+"
          label="Actividades creativas al año"
          icon={require('../assets/images/rocket.png')}
        />
        <StatCard
          number="10+"
          label="Profesores que enseñan con amor"
          icon={require('../assets/images/shapes.png')}
        />
      </View>
    </ScrollView>
  );
}
